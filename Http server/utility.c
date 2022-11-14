#define _POSIX_C_SOURCE 200112L
#include <netdb.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/stat.h>
#include <stdbool.h>
#include "utility.h"


#define NOT_FOUND 404
#define OK 200

void readHeader(int sockfd, char* path, int size) {
    char header[size];
    int n = read(sockfd, header, size - 1); // n is number of characters read
	if (n < 0) {
		perror("read in  readHeader");
		exit(EXIT_FAILURE);
	}
    // Null-terminate string
    header[n] = '\0';

    int method_size = 3; // "GET" method only takes up 3 bytes
    char method[3];
    int i = 0;
    // Retrive "GET" method to method[i]
    for (; header[i] != ' ' && i < method_size; i++) 
    {
        method[i] = header[i];
    }
    // Ensure the method is "GET"
    if (strcmp(method, "GET") != 0) {
        perror("Not a GET method");
		exit(EXIT_FAILURE);
    }
    ++i; // Remove the whitespace after "GET"
    // Retrive the path
    for (int k = 0; header[i] != ' ' && k < size; k++,i++) {
        path[k] = header[i];
    }
}

void handleRequest(int sockfd, char* path) {
    char relative_path[BUFSIZ] = { 0 };
    readHeader(sockfd, relative_path, BUFSIZ);
    if (strcmp(relative_path, "/") != 0 ) { // relative path exists
        strcat(path, "/");
        strcat(path, relative_path);
    }
    // Check if file exits
    struct stat st;
    if (stat(path, &st) == -1 || // File does not exist
        (strstr(path, "../") != NULL)) { // path escape
        // Send 404 response
        sendHeader(sockfd, NOT_FOUND, path);
    } else {
        // Send 200 response
        sendHeader(sockfd, OK, path);
        sendResource(sockfd, path);
    }
    close(sockfd);
}


void sendHeader(int sockfd, int status_code, char* filename) {
    char header[BUFSIZ] = { 0 };
    if (status_code == NOT_FOUND) {
        sprintf(header, "HTTP/1.0 404 Not Found\r\n");
    } else if (status_code == OK)
    {
        sprintf(header, "HTTP/1.0 200 OK\r\n");
    } else {
        perror("Invalid status code for the project");
        exit(EXIT_FAILURE);
    }
    write(sockfd, header, strlen(header));
    sprintf(header, "Content-Type: %s\r\n", getContentType(filename));
    write(sockfd, header, strlen(header));
    sprintf(header, "\r\n");
    write(sockfd, header, strlen(header));
}



void sendResource(int sockfd, char* filename) {
    FILE *fp = fopen(filename, "rb");

    // Check if the file exists
    if(!fp) {
        return;
    }
    while (!feof(fp)) { // Keeps reading file
        char buffer[BUFSIZ] = { 0 };
        int len = fread(buffer, sizeof(char), BUFSIZ, fp);
        write(sockfd, buffer, len);
    }
}


const char* getContentType(char* filename) {
    // Search string reversely
    char* content_type = NULL;
    bool has_dot = false;

    if (strstr(filename, ".") != NULL) {
        has_dot = true;
    }

    if (has_dot) {
        content_type = strrchr(filename, '.');
    }
    if (content_type == NULL) {
        return "application/octet-stream";
    } else if (strcmp(content_type, ".html") == 0) {
        return "text/html";
    } else if (strcmp(content_type, ".jpg") == 0){
        return "image/jpeg";
    } else if (strcmp(content_type, ".css") == 0) {
        return "text/css";
    } else if (strcmp(content_type, ".js") == 0) {
        return "text/javascript";
    }
    return "application/octet-stream";
}

size_t getFileSize(char* filename) {
    struct stat st;
    
    if (stat(filename, &st) != -1) {
        return st.st_size;
    }
    return -1;
}
