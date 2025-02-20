# Informatics Large Practical (ILP) - PizzaDronz Drone Delivery System

## Overview
This repository showcases my work on a drone delivery service simulation, **PizzaDronz**, developed as part of the **Informatics Large Practical** at the University of Edinburgh. The focus of the project was on solving real-world logistics challenges using state-of-the-art software engineering methodologies and a custom algorithmic approach.

The project involves designing and implementing an **autonomous drone delivery system** that efficiently delivers orders while navigating complex constraints such as no-fly zones, battery limits, and dynamic data inputs.

---

## Key Features

### 1. **Custom Pathfinding with Modified A***:
   - Developed a modified A* algorithm tailored for this project to calculate the drone's optimal route.
   - Incorporates:
     - Avoidance of no-fly zones defined by geospatial polygons.
     - Real-time constraints such as battery life, maximum delivery limits, and mandatory return points.
     - Latitude and longitude-based navigation using a simplified planar distance model.
   - Prioritizes both efficiency and safety, ensuring legal and efficient routes.

### 2. **Dynamic REST API Integration**:
   - Retrieves real-time data, including:
     - Orders, restaurants, and their operating schedules.
     - No-fly zones and central area boundaries.
   - Processes JSON responses to adapt drone behavior to daily order volumes and updated constraints.

### 3. **Geospatial Data Visualization**:
   - Generates GeoJSON files for visualizing drone flight paths, highlighting:
     - No-fly zone avoidance.
     - Optimized routes within Edinburgh's central area.
   - Compatible with visualization tools like [geojson.io](http://geojson.io).

### 4. **Robust Software Design**:
   - Built with **Java 18** features (streams, lambdas, records) for modern, maintainable code.
   - Implements comprehensive error handling and logging to ensure resilience in real-world scenarios.
   - Outputs deliverables in industry-standard formats for easy integration and testing.

---

## Technologies Used
- **Programming Language**: Java 18
- **Tools**: IntelliJ IDEA, Maven, Git
- **APIs & Libraries**:
  - Jackson for JSON parsing and deserialization.
  - GeoJSON format for spatial data representation.

---

## Project Deliverables
This project generates the following output files based on daily inputs:

1. **Delivery Status File (`deliveries-YYYY-MM-DD.json`)**:
   - Logs the status of each order (delivered, invalid, etc.) along with associated metadata.

2. **Drone Flight Path File (`flightpath-YYYY-MM-DD.json`)**:
   - Records the drone's move-by-move path, including angles, start/end coordinates, and active orders.

3. **GeoJSON Visualization File (`drone-YYYY-MM-DD.geojson`)**:
   - Visualizes the drone's route overlaid on a geospatial map, ensuring no-fly zone compliance.

