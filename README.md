# 🎭 3D Face Recognition System

<div align="center">

![Java](https://img.shields.io/badge/Java-11+-orange.svg)
![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)
![OpenCV](https://img.shields.io/badge/OpenCV-4.x-green.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

*Advanced 3D facial recognition system with real-time detection and intelligent recognition capabilities*

[Features](#-features) • [Installation](#-installation) • [Usage](#-usage) • [Documentation](#-project-structure)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Technology Stack](#️-technology-stack)
- [Installation](#-installation)
- [Usage](#-usage)
- [Project Structure](#-project-structure)
- [Classes & Components](#-classes--components)
- [Building the Project](#-building-the-project)
- [Configuration](#️-configuration)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🎯 Overview

A robust Java-based facial recognition system that implements 3D face detection and recognition using computer vision techniques. This system provides real-time face detection, feature extraction, and user authentication through advanced algorithms and machine learning models.

### Key Capabilities

- **Real-time face detection** from webcam or video sources
- **3D facial feature extraction** using advanced algorithms
- **Distance-based face matching** with multiple metrics
- **User enrollment and management** system
- **Interactive GUI** using JavaFX for easy operation
- **High performance** with optimized image processing

---

## ✨ Features

### Core Functionality

- 🎥 **Live Face Detection**: Real-time detection from webcam streams
- 🔍 **Face Recognition**: Identify registered users with high accuracy
- 📐 **Distance Measurement**: Multiple distance metrics (Euclidean, Cosine, Manhattan)
- 👤 **User Management**: Enroll, update, and delete user profiles
- 📊 **Feature Visualization**: Visual representation of facial features
- 💾 **Persistent Storage**: Save and load face databases

### Advanced Features

- 🌟 **Modern GUI**: JavaFX-based user interface with real-time preview
- 🖼️ **Image Processing**: Background removal and image enhancement
- 📈 **Feature Space Analysis**: Visualize and analyze facial feature vectors
- 🎨 **Custom Styling**: Modern dark theme interface
- ⚡ **Performance Optimized**: Efficient processing pipeline

---

## 🛠️ Technology Stack

### Core Technologies

- **Java 11+**: Primary programming language
- **JavaFX**: GUI framework for modern user interface
- **Maven**: Build automation and dependency management
- **OpenCV (via JavaCV)**: Computer vision and image processing

### Key Libraries

```xml
<dependencies>
    <!-- JavaCV for OpenCV bindings -->
    <dependency>
        <groupId>org.bytedeco</groupId>
        <artifactId>javacv-platform</artifactId>
    </dependency>
    
    <!-- JavaFX for GUI -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
    </dependency>
</dependencies>
```

---

## 📦 Installation

### Prerequisites

- **Java Development Kit (JDK) 11** or higher
- **Maven 3.6+** for building the project
- **Webcam** (for live face detection)
- **Minimum 4GB RAM** recommended

### Step 1: Clone the Repository

```bash
git clone https://github.com/ujjay2808/FaceSecure-3D-Intelligent-Facial-Recognition.git
cd FaceSecure-3D-Intelligent-Facial-Recognition
```

### Step 2: Install Dependencies

```bash
mvn clean install
```

### Step 3: Build the Project

```bash
mvn package assembly:single
```

This will create a JAR file with all dependencies in the `target/` directory.

---

## 🚀 Usage

### Running the Application

#### Using Maven:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="Main"
```

#### Using JAR file:

```bash
java -jar target/3d-face-recognition-1.0.0-jar-with-dependencies.jar
```

### Application Workflow

1. **Launch Application**: Start the program to open the main GUI
2. **Enroll New User**: 
   - Click "Enroll" button
   - Enter user details
   - Capture face images from webcam
   - System extracts and saves facial features
3. **Face Recognition**:
   - Click "Recognize" to start detection
   - System compares detected faces with database
   - Displays match results with confidence scores
4. **Browse Database**: View all enrolled users and their features

### Command Line Options

```bash
java -jar face-recognition.jar [options]

Options:
  --mode <enroll|recognize|browse>   Operation mode
  --source <camera_id>               Camera source (default: 0)
  --threshold <value>                Recognition threshold (0.0-1.0)
  --debug                            Enable debug mode
```

---

## 📁 Project Structure

```
face-recognition/
│
├── .github/
│   └── workflows/              # GitHub Actions CI/CD
│
├── .vscode/
│   └── settings.json          # VS Code configuration
│
├── lib/                       # External JAR libraries
│   ├── j3d-core.jar          # Java 3D core library
│   ├── j3d-utils.jar         # Java 3D utilities
│   ├── jai_codec.jar         # Java Advanced Imaging codec
│   ├── jai_core.jar          # Java Advanced Imaging core
│   ├── mlibwrapper_jai.jar   # Media library wrapper
│   └── vecmath.jar           # Vector math library
│
├── resources/                 # Application resources
│   └── target/               # Face database storage
│
├── scripts/
│   └── setup-libs.sh         # Library setup script
│
├── src/main/java/src/
│   ├── DistanceMeasure.java       # Distance metric calculations
│   ├── Face.java                  # Face data model
│   ├── FaceBrowser.java          # Face database browser GUI
│   ├── FeatureSpace.java         # Feature space representation
│   ├── FeatureSpaceVisualizer.java  # Feature visualization
│   ├── FeatureVector.java        # Feature vector operations
│   ├── FrontEnd.java             # Main application GUI
│   ├── ImageBackgroundPanel.java  # Custom image panel
│   ├── Main.java                 # Application entry point
│   ├── ModernStyles.java         # GUI styling
│   ├── Picture.java              # Image handling
│   └── TSCD.java                 # 3D face feature extraction
│
├── .gitignore
├── pom.xml                    # Maven configuration
└── README.md
```

---

## 🔧 Classes & Components

### Core Classes

#### `Main.java`
Application entry point that initializes the GUI and starts the JavaFX application.

#### `FrontEnd.java`
Main GUI controller that handles:
- Webcam integration
- Face detection and recognition
- User enrollment interface
- Real-time video display

#### `Face.java`
Data model representing a face with:
- User ID and name
- Feature vectors
- Facial measurements
- Timestamp information

#### `TSCD.java` (3D Feature Extractor)
Core algorithm for extracting 3D facial features:
- Processes facial landmarks
- Calculates spatial relationships
- Generates feature vectors for recognition

#### `FeatureVector.java`
Handles feature vector operations:
- Vector normalization
- Similarity calculations
- Feature space transformations

#### `DistanceMeasure.java`
Implements multiple distance metrics:
- **Euclidean Distance**: Standard L2 norm
- **Cosine Similarity**: Angular similarity measure
- **Manhattan Distance**: L1 norm
- **Chebyshev Distance**: Maximum coordinate difference

### GUI Components

#### `FaceBrowser.java`
Database browser for:
- Viewing enrolled users
- Displaying facial features
- Managing user profiles
- Deleting records

#### `FeatureSpaceVisualizer.java`
Visualizes feature space:
- 2D/3D scatter plots
- Clustering visualization
- Distance relationships

#### `ModernStyles.java`
Provides modern UI styling:
- Dark theme
- Custom color schemes
- Responsive layouts

---

## 🔨 Building the Project

### Maven Build Lifecycle

```bash
# Clean previous builds
mvn clean

# Compile source code
mvn compile

# Run tests
mvn test

# Create JAR package
mvn package

# Create JAR with dependencies
mvn assembly:single

# Install to local repository
mvn install
```

### Build Profiles

The project uses Maven profiles for different build configurations:

```bash
# Development build
mvn clean package -Pdev

# Production build with optimizations
mvn clean package -Pprod
```

---

## ⚙️ Configuration

### Application Settings

Create a `config.properties` file in the resources folder:

```properties
# Camera Settings
camera.id=0
camera.width=640
camera.height=480
camera.fps=30

# Recognition Settings
recognition.threshold=0.6
recognition.metric=euclidean
recognition.confidence.min=0.75

# Feature Extraction
features.vector.size=128
features.normalize=true

# Database
database.path=./resources/target
database.auto.save=true

# GUI
gui.theme=dark
gui.language=en
```

### Distance Metrics Configuration

```java
// In DistanceMeasure.java
public enum Metric {
    EUCLIDEAN,    // Best for general purpose
    COSINE,       // Best for normalized features
    MANHATTAN,    // Fast computation
    CHEBYSHEV     // Maximum difference
}
```

---

## 🎓 How It Works

### 1. Face Detection Pipeline

```
Webcam Input → Frame Capture → Face Detection → 
ROI Extraction → Preprocessing → Feature Extraction
```

### 2. Feature Extraction Process

The TSCD (3D) algorithm:
1. Detects facial landmarks (eyes, nose, mouth)
2. Calculates inter-landmark distances
3. Creates 3D spatial representation
4. Generates normalized feature vector

### 3. Recognition Process

```
Input Face → Feature Extraction → 
Compare with Database → Calculate Distance → 
Apply Threshold → Return Best Match
```

### 4. Distance Calculation

```java
// Example: Euclidean Distance
double distance = DistanceMeasure.euclidean(
    detectedFeatures, 
    storedFeatures
);

if (distance < threshold) {
    // Face recognized!
}
```

---

## 🧪 Testing

### Run Unit Tests

```bash
mvn test
```

### Test Coverage

```bash
mvn clean test jacoco:report
```

View coverage report at `target/site/jacoco/index.html`

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

### Development Setup

1. Fork the repository
2. Create a feature branch
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. Make your changes
4. Run tests
   ```bash
   mvn test
   ```
5. Commit with meaningful messages
   ```bash
   git commit -m "Add: amazing feature description"
   ```
6. Push to your fork
   ```bash
   git push origin feature/amazing-feature
   ```
7. Open a Pull Request

### Code Style

- Follow Java naming conventions
- Use meaningful variable names
- Add JavaDoc comments for public methods
- Keep methods focused and concise
- Write unit tests for new features

### Commit Message Format

```
Type: Brief description

Detailed explanation (optional)

Types: Add, Update, Fix, Remove, Refactor, Doc
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 Ujjay

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files...
```

---

## 🙏 Acknowledgments

- **OpenCV Team** for computer vision libraries
- **JavaCV Community** for Java bindings
- **Java 3D Project** for 3D visualization support

---

## 📧 Contact & Support

**Developer**: Ujjay  
**GitHub**: [@ujjay2808](https://github.com/ujjay2808)  
**Project Link**: [FaceSecure-3D](https://github.com/ujjay2808/FaceSecure-3D-Intelligent-Facial-Recognition)

### Getting Help

- 📖 [Documentation](docs/)
- 🐛 [Report Bug](https://github.com/ujjay2808/FaceSecure-3D-Intelligent-Facial-Recognition/issues)
- 💡 [Request Feature](https://github.com/ujjay2808/FaceSecure-3D-Intelligent-Facial-Recognition/issues)
- 💬 [Discussions](https://github.com/ujjay2808/FaceSecure-3D-Intelligent-Facial-Recognition/discussions)

---

## 📊 Project Status

![Build Status](https://img.shields.io/github/workflow/status/ujjay2808/FaceSecure-3D-Intelligent-Facial-Recognition/Java%20CI)
![Issues](https://img.shields.io/github/issues/ujjay2808/FaceSecure-3D-Intelligent-Facial-Recognition)
![Pull Requests](https://img.shields.io/github/issues-pr/ujjay2808/FaceSecure-3D-Intelligent-Facial-Recognition)
![Stars](https://img.shields.io/github/stars/ujjay2808/FaceSecure-3D-Intelligent-Facial-Recognition)

---

<div align="center">

**⭐ Star this repository if you find it helpful!**

Made by Ujjay

</div>
