package device;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;


import java.util.Optional;

public class Child extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private final String childId;

    private String value = "";

    private String prevValue = "";

    private Child(String childId) {
        this.childId = childId;
    }

    static Props props(String childId) {
        return Props.create(Child.class, () -> new Child(childId));
    }

    public static final class Сheck {
        public Сheck() {
        }
    }

    public static final class Transaction {
        public Transaction() {
        }
    }

    public static final class CanselTransaction {
        public CanselTransaction() {
        }
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
                        r -> getSender().tell(new Parent.ChildRegistered(), getSelf()))
                .match(Сheck.class,
                        r -> {
                            log.info("Readiness check: {}", childId);
                            getSender().tell(Parent.CheckPassed.class, getSelf());
                        })
                .match(Transaction.class,
                        r -> {
                            log.info("Readiness check: {}", childId);
                            getSender().tell(Parent.TransactionPassed.class, getSelf());
                        }).build();

    }
}