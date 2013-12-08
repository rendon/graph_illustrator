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
The input consist of two parts, edges and vertex info, the latter is optional.

The edge info section starts with the `[EDGES]` label. Each edge info has the following syntax:

    <start>,<end>,[label]

The vertex info section starts with the `[VERTICES]` label. Each vertex info has the following syntax:

    <node>:<x>,<y>

Examples:

input1.gi

    [EDGES]
    7,5,1
    2,1,2
    8,7,2
    2,4,1
    4,5,2
    9,5,1
    3,2,2
    2,10,1
    6,5,2

Execute:

    $ java -jar GraphIllustrator.jar < input1.gi

![Screenshot 1](https://raw.github.com/rendon/graph_illustrator/master/screenshots/screenshot1.png)

input2.gi

    [EDGES]
    alsa-lib-1.0.27.2,Firefox,
    GTK+-2.24.22,Firefox,
    Zip-3.0,Firefox,
     UnZip-6.0,Firefox,
    ATK-2.10.0,GTK+-2.24.22,
    gdk-pixbuf-2.30.1,GTK+-2.24.22,
    Pango-1.36.1,GTK+-2.24.22,
    Cairo-1.12.16,Pango-1.36.1,
    Xorg Libraries,Pango-1.36.1,
    [VERTICES]
    alsa-lib-1.0.27.2:-34.615220125786124,-5.169182389937074
    Firefox:1.9594968553459644,19.672201257861712
    GTK+-2.24.22:-10.647169811320662,-4.707169811320693
    Zip-3.0:18.802389937107083,-3.5613836477985927
     UnZip-6.0:52.61094339622657,-4.190314465408582
    ATK-2.10.0:-27.83761006289307,-21.474968553459103
    gdk-pixbuf-2.30.1:-7.962138364779845,-26.00566037735847
    Pango-1.36.1:22.075220125786196,-23.78503144654085
    Cairo-1.12.16:43.053584905660365,-36.897861635220096
    Xorg Libraries:11.268301886792516,-38.43471698113202

Execute:

    $ java -jar GraphIllustrator.jar < input2.gi


![Screenshot 2](https://raw.github.com/rendon/graph_illustrator/master/screenshots/screenshot2.png)

