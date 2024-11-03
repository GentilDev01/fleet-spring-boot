package com.kindsonthegenius.fleetmsv2.mailing;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public abstract class AbstractEmailContext {
    private String to;
    private String from;
    private String subject;
    private String email;
    private String templateLocation;
    private Map<String, Object> context;


    public String getTo() { return to; }
    public String getFrom() { return from; }
    public String getSubject() { return subject; }
    public String getTemplateLocation() { return templateLocation; }
    public Map<String, Object> getContext() { return context; }
    
    public void setTo(String to) { this.to = to; }
    public void setFrom(String from) { this.from = from; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setTemplateLocation(String location) { this.templateLocation = location; }

    public AbstractEmailContext() {
        this.context = new HashMap<>();
    }

    public <T> void init(T context) {
    }

    public Object put(String key, Object value) {
        return key == null ? null : this.context.put(key.intern(), value);
    }
}
