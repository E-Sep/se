package ap.projects.finalproject;

public class Book {
    private String title;
    private String author;
    private int publishYear;
    private String isbn;
    private String registeredBy; // Employee username who added this book
    private boolean isAvailable;

    public Book(String title, String author, int publishYear, String isbn, String registeredBy) {
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
        this.isbn = isbn;
        this.registeredBy = registeredBy;
        this.isAvailable = true;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return "Title: " + title +
                " | Author: " + author +
                " | Year: " + publishYear +
                " | ISBN: " + isbn +
                " | Status: " + (isAvailable ? "Available" : "On Loan");
    }
}