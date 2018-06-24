package net.igsoft.sdi;

public final class LaunchType extends ParametersBase {

    public static final ParametersBase MANUAL = new LaunchType(true);
    public static final ParametersBase AUTOMATIC = new LaunchType(false);

    private LaunchType(boolean manualStartAndStop) {
        super(manualStartAndStop);
    }

    @Override
    public String uniqueId() {
        //NOTE: manualStartAndStop does not differentiate instances of classes
        return "";
    }
}
