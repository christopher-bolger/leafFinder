# CA Exercise 1 – Autumn Leaves Identification / Vision System
**Data Structures and Algorithms 2 – 2026**

## Overview
Create a **JavaFX application** that analyses an image of an autumnal lawn/grass/field and automatically:

- Identifies individual leaves or clusters of leaves
- Estimates the number of leaves/clusters
- Highlights them using **blue rectangles**
- Allows users to specify colours of leaves to detect
- Converts the image to black‑and‑white for union‑find processing
- Provides tools to manage noise, thresholds, and image settings

The system may not always be perfect due to noise, lighting, and colour variation.

---

## Example Outputs
(Insert images here as needed)

---

## Implementation Requirements

### Black‑and‑White Conversion
- Convert the input image pixel-by-pixel using luminance/hue/saturation/brightness/RGB calculations.
- Leaf pixels → **white**, background → **black**.
- Users can select leaf colours (multiple allowed).
- Provide threshold controls for:
    - Hue
    - Saturation
    - Brightness
- Allow viewing of the B/W image in a separate window/pane.

### Union‑Find (Core Requirement)
- Treat each pixel as an initial disjoint set.
- Union all adjacent **white** pixels (up/down/left/right).
- Black pixels may be ignored.
- Optional: rescale image (e.g., 512×512) for faster compute and reduced noise.

### Identifying Leaf Clusters
- After union‑find, compute boundaries of each disjoint set.
- Draw **blue rectangles** on the original image.
- Display the pixel count of each leaf/cluster.
- Allow interactive inspection by clicking clusters.

### Cluster Ordering & Labelling
- Sort clusters by size (largest = **1**).
- Display sequential numbers on-screen.
- Provide access to cluster sizes.

### Visualising Disjoint Sets
Support both modes:
1. Click a cluster → highlight that disjoint set in the B/W image.
2. Randomly colour all disjoint sets in the B/W image.

### Travelling Salesman Path Animation
- Animate a path connecting all clusters.
- User chooses the starting cluster.
- Use any TSP approximation (e.g., nearest neighbour).
- Complete animation in ~5 seconds.
- Animation effect: rectangles briefly turn yellow in order.

### Noise Reduction & Outlier Management
- Allow minimum/maximum cluster size filtering.
- Provide threshold settings to improve B/W conversion.
- Support outlier detection (e.g., IQR‑based).

---

## JavaFX GUI Requirements
The GUI must let users:

- Choose leaf colours/shades (multiple).
- Select an image file.
- View original and B/W images.
- Run leaf recognition and draw rectangles.
- View cluster counts and sizes.
- Enable numbered cluster labels.
- Visualise disjoint sets (single or all).
- Animate the TSP path.
- Adjust noise/outlier thresholds.
- Navigate and exit cleanly.

---

## Marking Scheme (Total: 100%, CA worth 35%)

| Component | Marks |
|----------|-------|
| Image selection & display | 5% |
| B/W conversion & display | 8% |
| Union‑find implementation | 10% |
| On‑screen identification using rectangles | 10% |
| Ordered sequential numbering | 8% |
| Leaf/cluster counting | 7% |
| Reporting individual cluster sizes | 7% |
| Colouring disjoint sets | 8% |
| TSP path animation | 8% |
| Noise reduction & outlier management | 8% |
| JavaFX GUI | 5% |
| JUnit testing | 5% |
| JMH benchmarking | 5% |
| General quality (structure, logic, completeness) | 6% |

---

## Notes
Not all students are expected to complete all features. Use the marking scheme to prioritise your development.

