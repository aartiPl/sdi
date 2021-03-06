package net.igsoft.sdi.parameter;

public final class LaunchType extends ParameterBase {

    public static final LaunchType MANUAL = new LaunchType(true);
    public static final LaunchType AUTOMATIC = new LaunchType(false);

    private LaunchType(boolean manualStartAndStop) {
        super(manualStartAndStop);
    }

    @Override
    public String uniqueId() {
        //NOTE: manualStartAndStop does not differentiate instances of classes
        return "";
    }
}
