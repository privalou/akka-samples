package device;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class Main {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create();
        ActorRef parentActor = system.actorOf(Child.props("parent"));
        parentActor.tell(new Parent.RequestChild("child1"), ActorRef.noSender());
//        parentActor.tell(new Parent.RequestChild("child2"), ActorRef.noSender());
//        parentActor.tell(new Parent.RequestChild("child3"), ActorRef.noSender());
    }
}
