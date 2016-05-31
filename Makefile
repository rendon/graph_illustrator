.DEFAULT_GOAL := build

GRAPH_ILLUSTRATOR_HOME	:= /usr/local/graph_illustrator
CLASSESS				:= $(shell find . -name *.java)
PREFIX					:= /usr
JAVA					:= $(shell which java)
ANT						:= $(shell which ant)
install:
	- rm -f $(PREFIX)/bin/gi
	- rm -rf $(GRAPH_ILLUSTRATOR_HOME)
	mkdir -p $(GRAPH_ILLUSTRATOR_HOME)
	cp GraphIllustrator.jar $(GRAPH_ILLUSTRATOR_HOME)/
	cp bin/gi $(PREFIX)/bin/gi
	chmod +x $(PREFIX)/bin/gi

build: $(CLASSESS)
	ant jar
	mkdir -p bin/
	echo '#!/bin/bash' > bin/gi
	echo "$(JAVA) -jar $(GRAPH_ILLUSTRATOR_HOME)/GraphIllustrator.jar &" >> bin/gi
