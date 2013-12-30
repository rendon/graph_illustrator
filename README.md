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

    <node>:<x>,<y>[,label_aligment]
    label_alignment = L | C | R


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
    alsa-lib-1.0.27.2,Firefox
    GTK+-2.24.22,Firefox
    Zip-3.0,Firefox
    UnZip-6.0,Firefox
    ATK-2.10.0,GTK+-2.24.22
    gdk-pixbuf-2.30.1,GTK+-2.24.22
    Pango-1.36.1,GTK+-2.24.22
    Cairo-1.12.16,Pango-1.36.1
    Xorg Libraries,Pango-1.36.1
    GLib-2.36.4,ATK-2.10.0
    LibTIFF-4.0.3,gdk-pixbuf-2.30.1
    Harfbuzz-0.9.20,Pango-1.36.1
    libevent-2.0.21,Firefox
    libvpx-v1.2.0,Firefox
    yasm-1.2.0,libvpx-v1.2.0
    NSPR-4.10,Firefox
    NSS-3.15.1,Firefox,recomended
    SQLite-3.8.0.2,NSS-3.15.1
    [VERTICES]
    alsa-lib-1.0.27.2:-31.229972632963495,3.372027942771954
    Firefox:-31.331017090977042,32.279109722211345
    GTK+-2.24.22:-55.91817931979482,20.922353520139556
    Zip-3.0:-37.11919586532268,12.929888334762893
    UnZip-6.0:-24.047041973546854,12.701515187859657
    ATK-2.10.0:-65.087677773556,12.895996200206614
    gdk-pixbuf-2.30.1:-82.24864479026462,13.213945003895038
    Pango-1.36.1:-50.04081899601493,13.007280577090604
    Cairo-1.12.16:-50.88370357780594,-8.704693043291002
    Xorg Libraries:-59.841802174010695,-0.47646970754402673
    GLib-2.36.4:-65.3872579853671,6.211241952231539
    LibTIFF-4.0.3:-82.21793931954997,2.364807121555863
    Harfbuzz-0.9.20:-38.99396637358616,-13.36925150943452
    libevent-2.0.21:-4.444149544026786,12.856600935585014
    libvpx-v1.2.0:-10.168102409969492,3.13734545175236
    yasm-1.2.0:-9.968088347396659,-4.740131899689356
    NSPR-4.10:11.224166939473506,13.049122347440154
    NSS-3.15.1:25.686606789135453,12.875808450208062
    SQLite-3.8.0.2:25.70310133083322,-0.7260536576400227


Execute:

    $ java -jar GraphIllustrator.jar < input2.gi


![Screenshot 2](https://raw.github.com/rendon/graph_illustrator/master/screenshots/screenshot2.png)

