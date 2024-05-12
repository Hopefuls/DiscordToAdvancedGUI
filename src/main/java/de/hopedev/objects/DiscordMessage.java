package de.hopedev.objects;

public class DiscordMessage {
    private String author_name;
    private String message;
    public DiscordMessage(String author_name, String message) {
        this.author_name = author_name;
        this.message = message;
    }

    public String getAuthor() {
        return author_name;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return author_name + "> " + message;
    }
}
