package CDBurn.CDBurn;

public interface CDBurnWatcher {
    void setProgressValue(String message);
    void endRecord();
    void showError(String message);
}
