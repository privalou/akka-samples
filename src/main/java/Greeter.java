import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;


//#greeter-messages
public class Greeter extends AbstractActor {
    //#greeter-messages
    static Props props(String message, ActorRef printerActor) {
        return Props.create(Greeter.class, () -> new Greeter(message, printerActor));
    }

    //#greeter-messages
    static class WhoToGreet {
        final String who;

        private boolean flag;

        WhoToGreet(String who) {
            this.who = who;
        }
    }

    static class Greet {
        Greet() {
        }
    }
    //#greeter-messages

    private final String message;
    private final ActorRef printerActor;
    private String greeting = "";

    private Greeter(String message, ActorRef printerActor) {
        this.message = message;
        this.printerActor = printerActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(WhoToGreet.class, wtg -> {
                    this.greeting = message + ", " + wtg.who;
                })
                .match(Greet.class, x -> {
                    //#greeter-send-message
                    printerActor.tell(new Printer.Greeting(greeting), getSelf());
                    //#greeter-send-message
                })
                .build();
    }
//#greeter-messages
}
//#greeter-messages
