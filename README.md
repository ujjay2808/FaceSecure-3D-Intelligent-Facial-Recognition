💻 FaceSecure 3D: Intelligent Facial Recognition System
This is an advanced desktop application built in Java Swing for intelligent facial recognition. It utilizes the Eigenfaces algorithm (Principal Component Analysis) combined with k-Nearest Neighbors (k-NN) classification and a dynamic thresholding mechanism for robust identification, suitable for tasks like criminal face detection.

✨ Key Features
Secure Authentication: Includes a full login and registration system. User passwords are saved securely using SHA-256 hashing.

Eigenfaces Core: The system is trained by computing Eigenvectors (TSCD.java) from a set of face images, allowing for effective dimensionality reduction and feature extraction.

Dynamic k-NN Classification: Uses an enhanced k-NN algorithm with Euclidean distance and a dynamic threshold (set to 2.5x the average intra-class distance) to ensure high accuracy and reject unknown faces.

Automated Image Processing: Features an automatic process to recognize and crop face regions in training images, including steps for image normalization and grayscale conversion for consistent processing.

Modern Swing UI: The application features a clean, modern user interface, defined by ModernStyles.java, providing an engaging experience for training and probing the system.

Feature Space Visualization: Allows users to visualize recognition results by plotting the faces in a normalized 2D/3D feature space (Feature Dimension 1 vs. 2).

🛠️ Technology Stack
Category	Component	Detail	Source
Language	Java	JDK 11 is the minimum version specified in pom.xml	
GUI	Java Swing & AWT	Used for the desktop application interface (Login, Main UI, Controls)	
Core ML	Eigenfaces (PCA)	Implemented in TSCD.java using the Jama matrix library for decomposition	
Classification	k-NN	Classification performed in FeatureSpace.java	
Matrix Library	JAMA	Used for Eigenvalue Decomposition and Matrix operations.	
Database	MySQL	Configured for jdbc:mysql://localhost/3dface as the primary user backend.	
Image I/O	JAI (Java Advanced Imaging)	Core and Codec extensions used for image handling and processing.	


⚙️ Setup and Installation
This project is configured as a Maven project.

1. Prerequisites
Java Development Kit (JDK) 11 or higher.

Maven (for building the project).

MySQL Server (if you wish to use the database authentication).

2. Local Setup
Clone the Repository:

Bash

git clone https://github.com/ujjay2808/FaceSecure-3D-Intelligent-Facial-Recognition.git
cd FaceSecure-3D-Intelligent-Facial-Recognition
Install Dependencies: The project requires Java 3D and JAI libraries.

If the JAR files were not included in the initial push, you may need to run the setup script (Linux/macOS):

Bash

sh scripts/setup-libs.sh
Note: If you encounter issues, you may need to manually download the missing JARs (like JAI and Java3D-related files) and place them in the lib/ directory.

Build the Project: Use Maven to compile and create the executable JAR file.

Bash

mvn clean install
A runnable JAR (3d-face-recognition-1.0.0-jar-with-dependencies.jar) will be created in the target/ directory.

Database Configuration (Optional):

The application attempts to connect to a MySQL database named 3dface using root with an empty password.

If the database connection fails, the application falls back to reading/writing credentials in the local file users.txt.

🚀 Usage
The application is launched by running the main class, src.FrontEnd.

Start Application: Run the generated JAR or execute FrontEnd.java from your IDE.

Authentication: Use the GUI to Register a new user. The system will prompt for credentials, email, and phone number.

Load Training Data: After logging in, click "📁 Load Images". Select a directory containing subfolders, where each subfolder represents a different person (e.g., FaceImages/dhoni, FaceImages/sachin). The subfolder name will be automatically used as the Classification.

Train Model: Click "🧠 Compute Eigen Vectors" to begin the training process. This creates the feature space required for recognition.

Probe/Identify: Click "🔍 Identify Face" and select a single image (probe image) for identification against the trained database.

Visualize: Click "📊 Display Result Chart" to open the visualization window showing the probe face's location relative to the training data points in the feature space.
