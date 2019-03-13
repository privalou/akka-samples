package device;

import java.util.Map;
import java.util.HashMap;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.event.Logging;
import akka.event.LoggingAdapter;


public class Parent extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private final Map<String, ActorRef> childIdToActor = new HashMap<>();

    private final Map<ActorRef, String> actorToChildId = new HashMap<>();

    private int count = 0;

    private boolean status = true;


    public static Props props() {
        return Props.create(Parent.class, Parent::new);
    }

    static final class Start {
    }

    static final class ChildRegistered {
    }

    static final class CheckPassed {
    }

    static final class CheckFailed {
    }

    static final class TransactionPassed {
    }

    static final class TransactionFailed {
    }


    @Override
    public void preStart() {
        log.info("Parent started");
    }

    @Override
    public void postStop() {
        log.info("Parent stopped");
    }

    public static final class RequestChild {
        final String childId;

        public RequestChild(String childId) {
            this.childId = childId;
        }
    }

    private void run(Start start) {
        for (Map.Entry<String, ActorRef> child : childIdToActor.entrySet()) {
            ActorRef childActor = child.getValue();
            childActor.tell(new Child.Check(), getSelf());
        }
    }


    private void onTrackDevice(Parent.RequestChild trackMsg) {
        String childId = trackMsg.childId;
        ActorRef ref = childIdToActor.get(childId);
        if (ref != null) {
            ref.forward(trackMsg, getContext());
        } else {
            log.info("Creating child actor for {}", childId);
            ActorRef childActor = getContext().actorOf(Child.props(childId), "child-" + childId);
            getContext().watch(childActor);
            childIdToActor.put(childId, childActor);
            actorToChildId.put(childActor, childId);
            childActor.tell(trackMsg, getSelf());
        }
    }

    private void onTerminated(Terminated t) {
        ActorRef deviceActor = t.getActor();
        String deviceId = actorToChildId.get(deviceActor);
        log.info("Child actor for {} has been terminated", deviceId);
        actorToChildId.remove(deviceActor);
        childIdToActor.remove(deviceId);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Start.class, this::run)
                .match(Parent.RequestChild.class, this::onTrackDevice)
                .match(Terminated.class, this::onTerminated)
                .match(CheckPassed.class,
                        r -> {
                            log.info("Check passed");
                            count++;
                            if (count == childIdToActor.size()) {
                                count = 0;
                                if (status) {
                                    for (Map.Entry<String, ActorRef> child : childIdToActor.entrySet()) {
                                        ActorRef childActor = child.getValue();
                                        childActor.tell(new Child.Transaction(), getSelf());
                                    }
                                } else {
                                    log.info("END");
                                }
                            }

                        })
                .match(CheckFailed.class,
                        r -> {
                            log.info("Check failed");
                            status = false;
                        })
                .match(TransactionPassed.class,
                        r -> {
                            log.info("Transaction passed");
                            count++;
                            if (status && count == childIdToActor.size()) {
                                log.info("END TRANSACTION");
                            }
                        })
                .match(TransactionFailed.class,
                        r -> {
                            log.info("Transaction failed");
                            count++;
                            status = false;
                        })
                .build();
    }

}
