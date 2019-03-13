package device;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Child extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private final String childId;

    private boolean status = true;

    private String value = "";

    private String prevValue = "";

    private Child(String childId) {
        this.childId = childId;
    }

    static Props props(String childId) {
        return Props.create(Child.class, () -> new Child(childId));
    }

    public static final class Check {
    }

    public static final class Transaction {
    }

    public static final class CanselTransaction {
    }

    @Override
    public void preStart() {
        log.info("Child actor {} started", childId);
    }

    @Override
    public void postStop() {
        log.info("Child actor {} stopped", childId);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Parent.RequestChild.class,
                        r -> {
                            log.info("000000");
                            getSender().tell(new Parent.ChildRegistered(), getSelf());
                })
                .match(Check.class,
                        r -> {
                            log.info("Readiness check: {}", childId);
                            if (status) {
                                getSender().tell(new Parent.CheckPassed(), getSelf());
                            }
                            else {
                                getSender().tell(new Parent.CheckFailed(), getSelf());
                            }

                        })
                .match(Transaction.class,
                        r -> {
                            log.info("Conducting transaction: {}", childId);
                            getSender().tell(new Parent.TransactionPassed(), getSelf());
                        }).build();

    }
}