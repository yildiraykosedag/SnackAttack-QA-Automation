package pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * POST /contactMessages/save yanıtı (Deserialization):
 * {"message": "...", "status": "...", "object": {...}, "success": true}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactMessageResponse {

    private String message;
    private String status;
    private Boolean success;
    private ContactMessage object;

    public ContactMessageResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public ContactMessage getObject() {
        return object;
    }

    public void setObject(ContactMessage object) {
        this.object = object;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContactMessage {

        private Long id;
        private String name;
        private String email;
        private String subject;
        private String message;
        private String dateTime;

        public ContactMessage() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }
    }
}
