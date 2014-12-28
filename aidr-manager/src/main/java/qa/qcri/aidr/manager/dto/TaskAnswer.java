package qa.qcri.aidr.manager.dto;

public class TaskAnswer {

    private Long user_id;

    private DateHistory dateHistory;

    private TaskInfo info;

    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }

    public DateHistory getDateHistory() {
        return dateHistory;
    }

    public void setDateHistory(DateHistory dateHistory) {
        this.dateHistory = dateHistory;
    }

    public TaskInfo getInfo() {
        return info;
    }

    public void setInfo(TaskInfo info) {
        this.info = info;
    }
}
