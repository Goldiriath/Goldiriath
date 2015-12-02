package net.goldiriath.plugin.util.service;

public interface Service {

    public String getServiceId();

    public void start();

    public void stop();

    public boolean isStarted();

}
