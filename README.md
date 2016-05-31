Graph Illustrator
=================
Graph Illustrator is program to draw graphs (as in Graph Theory) interactively, lightweight and easy to use.

Problems involving graphs are much more easy to understand with a graphical representation. I developed this tool mostly for my self, but I think it can be helpful to others too.

Features
========
- Draw edges and vertices using the mouse.
- Custom colors for labels, background and borders.
- Own JSON based file format to save your drawings.
- Dragging of vertices using the mouse.
- Export to SVG.
- Zoom.
- Other features.

SGI Format
==========
Two formats are supported, GI (Graph Illustrator) and SGI (Simple Graph Illustrator), the latter is supported for convenience, many problems in competitive programming represent a graph in the form `<from> <to> [weight]`, for example:

```
0 1 5
1 2 1
3 1 8
0 3 7
4 0 9
```

Save this in a file with extension `.sgi` and this is the result:

![Simple](https://github.com/rendon/graph_illustrator/blob/master/doc/screenshots/simple.png)

Vertices' positions are set randomly. For anything else use the GI format.

GI Format
=========
This format uses JSON to represent the graph and all the necessary properties, is the recommended.

##Example 0
![Example 0](https://github.com/rendon/graph_illustrator/blob/master/doc/screenshots/example_0.png)

This is how a grah in GI format looks like:

```json
{
    "Graph": {
        "Edges": [
            {
                "backEdge": false,
                "center": {
                    "x": -2.29741,
                    "y": 1.666831
                },
                "directed": true,
                "end": 2,
                "foregroundColor": "0x000000ff",
                "highlighted": false,
                "label": "from a to b",
                "start": 1,
                "strokeColor": "0x000000ff"
            }
        ],
        "Vertices": [
            {
                "backgroundColor": "0xffffffff",
                "borderColor": "0x000000ff",
                "center": {
                    "x": -10.31746,
                    "y": 1.211362
                },
                "foregroundColor": "0x000000ff",
                "key": 1,
                "label": "a",
                "labelAlignment": "left",
                "radius": 1.428791
            },
            {
                "backgroundColor": "0xffffffff",
                "borderColor": "0x000000ff",
                "center": {
                    "x": 5.72264,
                    "y": 1.12782
                },
                "foregroundColor": "0x000000ff",
                "key": 2,
                "label": "b",
                "labelAlignment": "left",
                "radius": 1.428791
            }
        ]
    }
}
```

##Example 1
![Example 1](https://github.com/rendon/graph_illustrator/blob/master/doc/screenshots/example_1.png)

[Source](https://github.com/rendon/graph_illustrator/blob/master/doc/examples/example_1.gi)

##Example 2
![Example 2](https://github.com/rendon/graph_illustrator/blob/master/doc/screenshots/example_2.png)
[Source](https://github.com/rendon/graph_illustrator/blob/master/doc/examples/example_2.gi)

##Example 3
![Example 3](https://github.com/rendon/graph_illustrator/blob/master/doc/screenshots/example_3.png)
[Source](https://github.com/rendon/graph_illustrator/blob/master/doc/examples/example_3.gi)

Contributions and Bug reports
=============================
Contributions are welcome.

Bugs? For sure. Please report any bug you find, create an [issue](https://github.com/rendon/graph_illustrator/issues).
