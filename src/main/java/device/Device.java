package device;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;


import java.util.Optional;

public class Device extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);


    private final String deviceId;

    private Device(String deviceId) {
        this.deviceId = deviceId;
    }

    static Props props(String deviceId) {
        return Props.create(Device.class, () -> new Device(deviceId));
    }

    public static final class RecordTemperature {
        final long requestId;
        final double value;

        public RecordTemperature(long requestId, double value) {
            this.requestId = requestId;
            this.value = value;
        }
    }

    public static final class TemperatureRecorded {
        final long requestId;

        TemperatureRecorded(long requestId) {
            this.requestId = requestId;
        }
    }

    public static final class ReadTemperature {
        final long requestId;

        public ReadTemperature(long requestId) {
            this.requestId = requestId;
        }
    }

    public static final class RespondTemperature {
        final long requestId;
        final Optional<Double> value;

        RespondTemperature(long requestId, Optional<Double> value) {
            this.requestId = requestId;
            this.value = value;
        }
    }

    private Optional<Double> lastTemperatureReading = Optional.empty();

    @Override
    public void preStart() {
        log.info("Device actor {} started", deviceId);
    }

    @Override
    public void postStop() {
        log.info("Device actor {} stopped", deviceId);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(
                        DeviceGroup.RequestTrackDevice.class,
                        r -> {
                            if (this.deviceId.equals(r.deviceId)) {
                                getSender().tell(new DeviceGroup.DeviceRegistered(), getSelf());
                            } else {
                                log.warning(
                                        "Ignoring TrackDevice request for {}.This actor is responsible for {}.",
                                        r.deviceId,
                                        this.deviceId);
                            }
                        })
                .match(
                        RecordTemperature.class,
                        r -> {
                            log.info("Recorded temperature reading {} with {}", r.value, r.requestId);
                            lastTemperatureReading = Optional.of(r.value);
                            getSender().tell(new TemperatureRecorded(r.requestId), getSelf());
                        })
                .match(
                        ReadTemperature.class,
                        r -> getSender()
                                .tell(new RespondTemperature(r.requestId, lastTemperatureReading), getSelf()))
                .build();
    }
}