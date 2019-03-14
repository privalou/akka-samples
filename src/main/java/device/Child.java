package device;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Child extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private final String childId;

    private boolean status1 = true;

    private boolean status2 = true;

    private String value = "0";

    private String prevValue = "-1";

    private Child(String childId, boolean status1, boolean status2, String prevValue) {
        this.childId = childId;
        this.status1 = status1;
        this.status2 = status2;
        this.prevValue = prevValue;
    }

    static Props props(String childId, boolean status1, boolean status2, String prevValue) {
        return Props.create(Child.class, () -> new Child(childId, status1, status2, prevValue));
    }

    public static final class Check {
    }

    public static final class Transaction {
    }

    public static final class CancelTransaction {
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
                            if (status1) {
                                getSender().tell(new Parent.CheckPassed(), getSelf());
                            }
                            else {
                                getSender().tell(new Parent.CheckFailed(), getSelf());
                            }

                        })
                .match(Transaction.class,
                        r -> {
                            log.info("Conducting transaction: {}", childId);
                            if (status2) {
                                log.info("Transaction successful: {}", childId);
                                value = "1";
                                log.info("Value: {}", value);
                                getSender().tell(new Parent.TransactionPassed(), getSelf());
                            }
                            else {
                                log.info("Transaction failed: {}", childId);
                                getSender().tell(new Parent.TransactionFailed(), getSelf());
                            }

                        })
                .match(CancelTransaction.class,
                        r -> {
                            log.info("Cancel transaction: {}", childId);
                            value = prevValue;
                            log.info("Value: {}", value);

                        })
                .build();

    }
}