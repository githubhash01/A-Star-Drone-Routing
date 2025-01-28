# Informatics Large Practical (ILP) - University of Edinburgh

## Overview
This repository contains the source code, documentation, and deliverables from my successful completion of the **Informatics Large Practical (ILP)** course at the University of Edinburgh (2023–2024). This Level 9, 20-credit course focuses on advanced software engineering practices through a real-world, individual project.

The central project is the development of a Java-based application for a simulated drone delivery service, **PizzaDronz**. The project emphasizes **design**, **implementation**, and **reflection** in software engineering, requiring robust coding, essay-based reasoning, and state-of-the-art methodologies.

---

## Project Scope
The project involved creating a drone delivery service capable of:
- **Planning and optimizing delivery routes** within constraints like battery life, no-fly zones, and restaurant operating hours.
- **Processing RESTful data**, including dynamic JSON inputs for orders, no-fly zones, and restaurant menus.
- Generating output files for visualizing drone flight paths and documenting deliveries.

### Core Deliverables:
1. **Coursework 1 (CW1)**:
   - Implemented initial design components using a provided JAR library.
   - Wrote an essay explaining design decisions and future planning.

2. **Coursework 2 (CW2)**:
   - Developed the full Java application.
   - Processed live REST API data to calculate feasible flight paths.
   - Generated three output files:
     - Delivery status (`deliveries-YYYY-MM-DD.json`)
     - Detailed flight paths (`flightpath-YYYY-MM-DD.json`)
     - Visual flight paths in GeoJSON (`drone-YYYY-MM-DD.geojson`).

---

## Key Features
- **Data-Driven Architecture**:
  - Designed to adapt to dynamic REST API inputs.
  - Ensures flexibility for future enhancements.

- **Algorithmic Drone Routing**:
  - Implements a constraint-driven approach for optimal deliveries.
  - Avoids no-fly zones while adhering to specific business rules.

- **Code Quality**:
  - Robust, modular design with Java 18 features (streams, lambdas).
  - Comprehensive documentation and error handling.

- **Visual Insights**:
  - GeoJSON flight paths visualized using [geojson.io](http://geojson.io).

---

## Technologies Used
- **Programming Language**: Java 18
- **Development Tools**: IntelliJ IDEA, Maven, Git
- **APIs & Libraries**:
  - RESTful APIs for data retrieval.
  - Jackson for JSON parsing.
  - GeoJSON format for spatial data visualization.

---

## Repository Structure
```plaintext
📂 src/
   ├── main/
   │   ├── java/       # Java source code
   │   └── resources/  # Configuration files
   └── test/           # Unit tests
📂 resultfiles/         # Generated output files (JSON, GeoJSON)
📄 README.md            # Project documentation
