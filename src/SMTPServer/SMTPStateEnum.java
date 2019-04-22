package SMTPServer;

public enum SMTPStateEnum {
    STOPPED,
    READY,
    WAITING_EHLO,
    WAITING_MAIL,
    WAITING_RECIPIENT,
    WAITING_DATA
}