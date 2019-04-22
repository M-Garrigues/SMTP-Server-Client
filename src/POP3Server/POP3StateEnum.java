package POP3Server;

public enum POP3StateEnum {
    STOPPED,
    READY,
    AUTHORIZATION,
    WAITING_PASSWORD,
    TRANSACTION
}