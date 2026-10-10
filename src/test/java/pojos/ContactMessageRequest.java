package pojos;

/** POST /contactMessages/save istek gövdesi (Serialization). Sınırlar: name 2-20, email 5-25, subject 3-55, message 3-150. */
public class ContactMessageRequest {

    private String name;
    private String email;
    private String subject;
    private String message;

    public ContactMessageRequest() {
    }

    public ContactMessageRequest(String name, String email, String subject, String message) {
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
