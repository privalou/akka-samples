package device;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class Main {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create();
        ActorRef parentActor = system.actorOf(Parent.props());
        parentActor.tell(new Parent.RequestChild("child1", true, true, "-1"), ActorRef.noSender());
        parentActor.tell(new Parent.RequestChild("child2", true, true, "-1"), ActorRef.noSender());
        parentActor.tell(new Parent.RequestChild("child3", true, false, "-1"), ActorRef.noSender());

        parentActor.tell(new Parent.Start(), ActorRef.noSender());
    }
}
