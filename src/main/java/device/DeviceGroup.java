package device;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.event.Logging;
import akka.event.LoggingAdapter;


// #device-group-full
// #device-group-remove
// #device-group-register
public class DeviceGroup extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);


    public static Props props() {
        return Props.create(DeviceGroup.class, DeviceGroup::new);
    }
    // #device-group-register
    // #device-group-remove

    public static final class RequestDeviceList {
        final long requestId;

        public RequestDeviceList(long requestId) {
            this.requestId = requestId;
        }
    }

    public static final class ReplyDeviceList {
        final long requestId;
        final Set<String> ids;

        public ReplyDeviceList(long requestId, Set<String> ids) {
            this.requestId = requestId;
            this.ids = ids;
        }
    }
    // #device-group-remove
    // #device-group-register

    static final class DeviceRegistered {
    }

    private final Map<String, ActorRef> deviceIdToActor = new HashMap<>();
    // #device-group-register
    private final Map<ActorRef, String> actorToDeviceId = new HashMap<>();
    // #device-group-register

    @Override
    public void preStart() {
        log.info("DeviceGroup started");
    }

    @Override
    public void postStop() {
        log.info("DeviceGroup stopped");
    }

    public static final class RequestTrackDevice {
        final String deviceId;

        public RequestTrackDevice(String deviceId) {
            this.deviceId = deviceId;
        }
    }


    private void onTrackDevice(RequestTrackDevice trackMsg) {
        String deviceId = trackMsg.deviceId;
        ActorRef ref = deviceIdToActor.get(deviceId);
        if (ref != null) {
            ref.forward(trackMsg, getContext());
        } else {
            log.info("Creating device actor for {}", deviceId);
            ActorRef deviceActor = getContext().actorOf(Device.props(deviceId), "device-" + deviceId);
            getContext().watch(deviceActor);
            deviceActor.forward(trackMsg, getContext());
            deviceIdToActor.put(deviceId, deviceActor);
            actorToDeviceId.put(deviceActor, deviceId);
        }
    }

    private void onTerminated(Terminated t) {
        ActorRef deviceActor = t.getActor();
        String deviceId = actorToDeviceId.get(deviceActor);
        log.info("Device actor for {} has been terminated", deviceId);
        actorToDeviceId.remove(deviceActor);
        deviceIdToActor.remove(deviceId);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RequestTrackDevice.class, this::onTrackDevice)
                .match(Terminated.class, this::onTerminated)
                .build();
    }
}
