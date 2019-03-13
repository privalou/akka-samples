import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.io.IOException;

public class AkkaQuickstart {
    public static void main(String[] args) throws IOException {
        final ActorSystem system = ActorSystem.create("helloakka");
        try {
            //#create-actors
            final ActorRef headNode =
                    system.actorOf(Printer.props(), "headNode");
            final ActorRef firstNode =
                    system.actorOf(Greeter.props("Ready", headNode), "firstNode");
            final ActorRef secondNode =
                    system.actorOf(Greeter.props("Ready", headNode), "secondNode");
            final ActorRef thirdNode =
                    system.actorOf(Greeter.props("Not ready", headNode), "thirdNode");
            //#create-actors


//            headNode.tell();

            //#main-send-messages
            firstNode.tell(new Greeter.WhoToGreet("headNode"), ActorRef.noSender());
            firstNode.tell(new Greeter.Greet(), ActorRef.noSender());

            firstNode.tell(new Greeter.WhoToGreet("headNode"), ActorRef.noSender());
            firstNode.tell(new Greeter.Greet(), ActorRef.noSender());

            secondNode.tell(new Greeter.WhoToGreet("headNode"), ActorRef.noSender());
            secondNode.tell(new Greeter.Greet(), ActorRef.noSender());

            thirdNode.tell(new Greeter.WhoToGreet("error"), ActorRef.noSender());
            thirdNode.tell(new Greeter.Greet(), ActorRef.noSender());
            //#main-send-messages

            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } finally {
            system.terminate();
        }
    }
}
