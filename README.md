Graph Illustrator
=================
This tool reads a collection of relationships (u --> v) from the standard input a creates a graphical representation.

I've created this tool mainly by two reasons:
- There are many problems involving graphs and it is much more easy to understand the problem with a graphical representation.
- I could not found a any tool that suit my needs.

Features
========
- Reads relationships from STDIN (keyboard), although you can always read from files using pipelining.
- Vertex Dragging - The positions of the vertices are generated randomly so you have to drag the vertices as you like (mouse's left button).
- Plane Dragging - Use the mouse's right button to drag the drawing area.
- Zoom - sometimes we need a better perspective (use the mouse wheel).

For now these features are good for me but I'll add more features and improvements as needed.

How to use
==========
Each line of the input has the following syntax:

    <start> <end> [label]

Examples:
input1.txt

    1 3 foo
    2 5
    3 5 3

Execute:

    $ java -jar GraphIllustrator.jar < input1.txt

![Screenshot 1](https://raw.github.com/rendon/graph_illustrator/master/screenshots/screenshot1.png)

input2.txt

    MX USA   1000
    FR  ES  2000
    RU CN   5000
    RU MX   8500
    RU ES   2200

Execute:

    $ java -jar GraphIllustrator.jar < input2.txt


![Screenshot 2](https://raw.github.com/rendon/graph_illustrator/master/screenshots/screenshot2.png)

