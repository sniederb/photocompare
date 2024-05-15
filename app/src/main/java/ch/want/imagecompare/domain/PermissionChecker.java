package ch.want.imagecompare.domain;

public interface PermissionChecker {

    boolean hasPermissions();

    void askNicely(boolean forceCheck);

    boolean supportsReselect();
}
