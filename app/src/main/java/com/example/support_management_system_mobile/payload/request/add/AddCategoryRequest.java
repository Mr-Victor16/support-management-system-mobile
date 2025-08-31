package com.example.support_management_system_mobile.payload.request.add;

public class AddCategoryRequest {
    private String name;

    public AddCategoryRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class AddTicketReplyRequest {
        private Long ticketID;
        private String content;

        public AddTicketReplyRequest(Long ticketID, String content) {
            this.ticketID = ticketID;
            this.content = content;
        }

        public Long getTicketID() {
            return ticketID;
        }

        public void setTicketID(Long ticketID) {
            this.ticketID = ticketID;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class AddTicketRequest {
        private String title;
        private String description;
        private Long categoryID;
        private Long priorityID;
        private String version;
        private Long softwareID;

        public AddTicketRequest(String title, String description, Long categoryID, Long priorityID, String version, Long softwareID) {
            this.title = title;
            this.description = description;
            this.categoryID = categoryID;
            this.priorityID = priorityID;
            this.version = version;
            this.softwareID = softwareID;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Long getCategoryID() {
            return categoryID;
        }

        public void setCategoryID(Long categoryID) {
            this.categoryID = categoryID;
        }

        public Long getPriorityID() {
            return priorityID;
        }

        public void setPriorityID(Long priorityID) {
            this.priorityID = priorityID;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Long getSoftwareID() {
            return softwareID;
        }

        public void setSoftwareID(Long softwareID) {
            this.softwareID = softwareID;
        }
    }
}
