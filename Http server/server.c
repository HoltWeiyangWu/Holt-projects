// Assignment2 Weiyang Wu
// Adapted from the COMP30023 workshop 9 practical code in server.c
// Reference: Beej's networking guide, man pages
// Reference: Week8 Lecture 2 Create IPv6 socket
#define _POSIX_C_SOURCE 200112L
#include <netdb.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <ctype.h>
#include <stdbool.h>
#include <pthread.h>
#include "utility.h"

#define MAX_PORT_VAL 65535 // 2^16 -1
#define MIN_PORT_VAL 0
#define NUM_ARG 3
#define IPV4 "4"
#define IPV6 "6"
#define IMPLEMENTS_IPV6

int main(int argc, char** argv) {
	int sockfd, newsockfd, re, s;
	//char buffer[BUFSIZ];
	struct addrinfo hints, *res, *resIPv6;
	struct sockaddr_storage client_addr;
	socklen_t client_addr_size;

	if (argc < NUM_ARG) {
		fprintf(stderr, "usage: [protocol number] [port number] [path to web root]\n");
		exit(EXIT_FAILURE);
	}

	char* protocol_number = argv[1];
    char* port_number = argv[2];
    char* path = argv[3];

	// Convert string to int for port number argument
	int int_port_num = strtol(port_number, NULL, 10);
	if (int_port_num > MAX_PORT_VAL ||
		int_port_num < MIN_PORT_VAL) { // Verify port number
			perror("Invalid port number");
			exit(EXIT_FAILURE);
		}

	// Create address we're going to listen on (with given port number)
	memset(&hints, 0, sizeof hints);

	// Verify protocal number
	if (strcmp(protocol_number, IPV4) == 0) {
		hints.ai_family = AF_INET;       // IPv4
	} else if (strcmp(protocol_number, IPV6) == 0) {
		hints.ai_family = AF_INET6;      // IPv6
	} else {
		perror("Invalid protocol number");
		exit(EXIT_FAILURE);
	}

	hints.ai_socktype = SOCK_STREAM; // TCP
	hints.ai_flags = AI_PASSIVE;     // for bind, listen, accept
	// node (NULL means any interface), service (port), hints, res
	s = getaddrinfo(NULL, port_number, &hints, &res);
	if (s != 0) {
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(s));
		exit(EXIT_FAILURE);
	}

	// Create socket according to protocal number
	if (strcmp(protocol_number, IPV6) == 0) {
		for (resIPv6 = res; resIPv6 != NULL; resIPv6 = resIPv6->ai_next) {
			if (resIPv6->ai_family == AF_INET6){
				if ((sockfd = socket(resIPv6->ai_family,
								resIPv6->ai_socktype, 
								resIPv6->ai_protocol)) > 0) {
					res = resIPv6;
				} else {
					perror("Fail to create socket IPv6");
					exit(EXIT_FAILURE);
				}
			}
		}
	} else if (strcmp(protocol_number, IPV4) == 0) {
		sockfd = socket(res->ai_family, res->ai_socktype, res->ai_protocol);
	}


	if (sockfd < 0) {
		perror("socket");
		exit(EXIT_FAILURE);
	}

	// Reuse port if possible
	re = 1;
	if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &re, sizeof(int)) < 0) {
		perror("setsockopt");
		exit(EXIT_FAILURE);
	}

	// Bind address to the socket
	if (bind(sockfd, res->ai_addr, res->ai_addrlen) < 0) {
		perror("bind");
		exit(EXIT_FAILURE);
	}
	freeaddrinfo(res);

	// Listen on socket - means we're ready to accept connections,
	// incoming connection requests will be queued, man 3 listen
	if (listen(sockfd, 5) < 0) {
		perror("listen");
		exit(EXIT_FAILURE);
	}

	// Accept a connection - blocks until a connection is ready to be accepted
	// Get back a new file descriptor to communicate on
	while (true) {
		client_addr_size = sizeof client_addr;
		newsockfd =
			accept(sockfd, (struct sockaddr*)&client_addr, &client_addr_size);
		if (newsockfd < 0) {
			perror("accept");
			exit(EXIT_FAILURE);
		}

		handleRequest(newsockfd, path);
		path = argv[3];


	}
	close(sockfd);
	return 0;
}
