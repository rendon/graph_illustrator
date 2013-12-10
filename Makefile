GRAPH_ILLUSTRATOR_HOME=/opt/graph_illustrator
PREFIX=/usr
install:
	- rm $(PREFIX)/bin/gi
	- rm -r $(GRAPH_ILLUSTRATOR_HOME)
	mkdir $(GRAPH_ILLUSTRATOR_HOME)
	cp GraphIllustrator.jar $(GRAPH_ILLUSTRATOR_HOME)
	echo '#!/bin/bash' > $(PREFIX)/bin/gi
	echo "java -jar $(GRAPH_ILLUSTRATOR_HOME)/GraphIllustrator.jar &" >> $(PREFIX)/bin/gi
	chmod +x $(PREFIX)/bin/gi

