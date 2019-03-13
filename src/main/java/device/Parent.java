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
            childActor.forward(Child.Сheck.class, getContext());
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
            childActor.forward(trackMsg, getContext());
            childIdToActor.put(childId, childActor);
            actorToChildId.put(childActor, childId);
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
                            log.info("111111");
                        })
                .match(TransactionPassed.class,
                        r -> {
                            log.info("222222");
                        })
                .build();
    }

}
