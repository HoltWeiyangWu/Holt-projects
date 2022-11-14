#ifndef UTILITY_H
#define UTILITY_H

// Take a file descriptor, a char list and a list size
// Read GET request header and return the path to the char list
void readHeader(int sockfd, char* path, int size);

// Handle requests from clients.
void handleRequest(int sockfd, char* path);

// Take a status code and send the corresponding
// header to the client
void sendHeader(int sockfd, int status_code, char* filename);

// Take a path and send the corresponding
// file to the client
void sendResource(int sockfd, char* filename);

const char* getContentType(char* filename);

size_t getFileSize(char* filename);

#endif
