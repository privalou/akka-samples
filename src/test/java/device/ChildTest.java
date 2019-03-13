package device;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.scalatest.junit.JUnitSuite;


public class ChildTest extends JUnitSuite {

    private static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testReplyToRegistrationRequests() {
        TestKit probe = new TestKit(system);
        ActorRef deviceActor = system.actorOf(Child.props("parent"));
        deviceActor.tell(new Parent.RequestChild("child"), probe.getRef());
        probe.expectMsgClass(Parent.ChildRegistered.class);
        assertEquals(deviceActor, probe.getLastSender());
    }

    @Test
    public void testIgnoreWrongRegistrationRequests() {
//        TestKit probe = new TestKit(system);
//        ActorRef childActor = system.actorOf(Child.props("child"));
//        childActor.tell(new Parent.RequestChild("wrongChild"), probe.getRef());
//        probe.expectNoMessage();
    }

    @Test
    public void testReplyWithEmptyReadingIfNoTemperatureIsKnown() {
//        TestKit probe = new TestKit(system);
//        ActorRef childActor = system.actorOf(Child.props("child"));
//        childActor.tell(new Child.ReadTemperature(42L), probe.getRef());
//        Child.RespondTemperature response = probe.expectMsgClass(Child.RespondTemperature.class);
//        assertEquals(42L, response.requestId);
//        assertEquals(Optional.empty(), response.value);
    }

    @Test
    public void testReplyWithLatestTemperatureReading() {
//        TestKit probe = new TestKit(system);
//        ActorRef deviceActor = system.actorOf(Child.props("device"));
//
//        deviceActor.tell(new Child.RecordTemperature(1L, 24.0), probe.getRef());
//        assertEquals(1L, probe.expectMsgClass(Child.TemperatureRecorded.class).requestId);
//
//        deviceActor.tell(new Child.ReadTemperature(2L), probe.getRef());
//        Child.RespondTemperature response1 = probe.expectMsgClass(Child.RespondTemperature.class);
//        assertEquals(2L, response1.requestId);
//        assertEquals(Optional.of(24.0), response1.value);
//
//        deviceActor.tell(new Child.RecordTemperature(3L, 55.0), probe.getRef());
//        assertEquals(3L, probe.expectMsgClass(Child.TemperatureRecorded.class).requestId);
//
//        deviceActor.tell(new Child.ReadTemperature(4L), probe.getRef());
//        Child.RespondTemperature response2 = probe.expectMsgClass(Child.RespondTemperature.class);
//        assertEquals(4L, response2.requestId);
//        assertEquals(Optional.of(55.0), response2.value);
    }
    // #device-write-read-test

}