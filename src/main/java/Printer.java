import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

//#printer-messages
public class Printer extends AbstractActor {
    //#printer-messages
    static Props props() {
        return Props.create(Printer.class, Printer::new);
    }

    //#printer-messages
    static class Greeting {
        final String message;

        Greeting(String message) {
            this.message = message;
        }
    }
    //#printer-messages

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private Printer() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Greeting.class, greeting -> {
                    log.info(greeting.message);
                })
                .build();
    }
//#printer-messages
}
//#printer-messages
