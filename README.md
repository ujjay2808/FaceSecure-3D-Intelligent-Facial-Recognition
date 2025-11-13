FaceSecure 3D: Intelligent Facial Recognition System 🚀
https://img.shields.io/badge/Java-11%252B-blue
https://img.shields.io/badge/GUI-Java%2520Swing-green
https://img.shields.io/badge/ML-Eigenfaces%2520(PCA)-orange
https://img.shields.io/badge/License-MIT-yellow

An advanced desktop application built in Java Swing for intelligent facial recognition using the Eigenfaces algorithm (Principal Component Analysis) combined with k-Nearest Neighbors classification and dynamic thresholding.

✨ Key Features
🔐 Secure Authentication
Complete login and registration system

SHA-256 hashing for secure password storage

Fallback to local file storage if database is unavailable

🧠 Advanced Machine Learning
Eigenfaces Algorithm: PCA-based feature extraction implemented in TSCD.java

Dynamic k-NN Classification: Enhanced k-NN with Euclidean distance and adaptive thresholding

Automatic Face Processing: Intelligent cropping, normalization, and grayscale conversion

Unknown Face Rejection: Dynamic threshold set to 2.5x average intra-class distance

🎨 Modern User Experience
Clean, modern Swing UI styled with ModernStyles.java

Intuitive workflow for training and recognition

Real-time visualization of recognition results

📊 Visualization & Analytics
2D/3D feature space visualization

Plotting of faces in normalized feature dimensions

Interactive result charts for analysis

🛠️ Technology Stack
Category	Component	Details
Language	Java	JDK 11+
GUI Framework	Java Swing & AWT	Modern desktop interface
Core Algorithm	Eigenfaces (PCA)	TSCD.java with JAMA matrix library
Classification	k-Nearest Neighbors	Enhanced with dynamic thresholding
Matrix Operations	JAMA	Eigenvalue decomposition & matrix math
Database	MySQL	jdbc:mysql://localhost/3dface
Image Processing	JAI (Java Advanced Imaging)	Core and Codec extensions
📦 Project Structure
text
FaceSecure-3D-Intelligent-Facial-Recognition/
├── src/
│   ├── FrontEnd.java                 # Main application entry point
│   ├── TSCD.java                     # Eigenfaces/PCA implementation
│   ├── FeatureSpace.java             # k-NN classification & feature space
│   ├── ModernStyles.java             # UI styling and themes
│   └── [Other core classes...]
├── lib/
│   ├── jai_core.jar                  # Java Advanced Imaging
│   ├── jai_codec.jar                 # Image codec support
│   └── [Other dependencies...]
├── scripts/
│   └── setup-libs.sh                 # Dependency setup script
├── target/
│   └── 3d-face-recognition-1.0.0-jar-with-dependencies.jar
├── pom.xml                           # Maven configuration
└── README.md
⚙️ Setup and Installation
Prerequisites
Java Development Kit (JDK) 11 or higher

Maven (for building the project)

MySQL Server (optional, for database authentication)

Installation Steps
Clone the Repository

bash
git clone https://github.com/ujjay2808/FaceSecure-3D-Intelligent-Facial-Recognition.git
cd FaceSecure-3D-Intelligent-Facial-Recognition
Install Dependencies

bash
# Run the setup script (Linux/macOS)
sh scripts/setup-libs.sh

# Windows users may need to manually place JARs in lib/ directory
# Required JARs: JAI Core, JAI Codec, Java3D libraries
Build with Maven

bash
mvn clean install
This creates target/3d-face-recognition-1.0.0-jar-with-dependencies.jar

Database Setup (Optional)

The application connects to MySQL database 3dface

Default credentials: root with empty password

If connection fails, falls back to local users.txt file

🚀 Usage Guide
Starting the Application
bash
java -jar target/3d-face-recognition-1.0.0-jar-with-dependencies.jar
Or run src.FrontEnd directly from your IDE.

🔐 Authentication
Register: Create new user account with credentials, email, and phone number

Login: Secure authentication with SHA-256 hashed passwords

🧠 Training Workflow
Load Training Data: Click "📁 Load Images"

Select directory with person-specific subfolders

Example: FaceImages/dhoni/, FaceImages/sachin/

Subfolder names become classification labels

Train Model: Click "🧠 Compute Eigen Vectors"

System processes images and creates feature space

Generates Eigenvectors for recognition

🔍 Recognition Process
Identify Faces: Click "🔍 Identify Face"

Select Probe Image: Choose image for identification

View Results: System matches against trained database

📊 Visualization
Click "📊 Display Result Chart" to open visualization

See probe face location relative to training data

Analyze feature space in 2D/3D dimensions

🎯 Algorithm Details
Eigenfaces Implementation
Dimensionality Reduction: PCA transforms face images to lower-dimensional space

Feature Extraction: Captures most significant facial variations

Efficient Storage: Compact representation of facial features

Dynamic k-NN Classification
Adaptive Thresholding: 2.5x average intra-class distance for unknown face rejection

Euclidean Distance: Similarity measurement in feature space

Robust Matching: Handles variations in lighting and expression

🤝 Contributing
We welcome contributions! Please follow these steps:

Fork the project

Create your feature branch (git checkout -b feature/AmazingFeature)

Commit your changes (git commit -m 'Add some AmazingFeature')

Push to the branch (git push origin feature/AmazingFeature)

Open a Pull Request

📜 License
This project is licensed under the MIT License - see the LICENSE file for details.

👨‍💻 Author
Ujjay Manety

GitHub: @ujjay2808

🙏 Acknowledgments
Eigenfaces algorithm based on Turk and Pentland research

JAMA matrix library for mathematical operations

Java Swing community for UI components

JAI team for image processing capabilities

⭐ Star this repo if you find it useful!

