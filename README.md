# APPLIED MACHINE LEARNING – MID 2  
## M.Tech CSE – I Semester  

---

## Question 1: Describe Support Vector Machines (SVM) in detail  
*(10 Marks)*

### Introduction
Support Vector Machine (SVM) is a **supervised machine learning algorithm** mainly used for **classification** and also for **regression** problems. The main aim of SVM is to separate data points belonging to different classes using a decision boundary in the best possible way. SVM is known for its good performance even when the dataset size is small.

---

### Basic Idea of SVM
The basic idea of SVM is to find a boundary that separates the classes with the **maximum margin**.  
- The separating boundary is called a **hyperplane**  
- The margin is the distance between the hyperplane and the nearest data points from each class  
- A larger margin leads to better accuracy on unseen data  

---

### Hyperplane
A hyperplane is a line or surface that divides the data into different classes.
- In 2D, it is a straight line  
- In 3D, it is a plane  
- In higher dimensions, it is called a hyperplane  

Mathematically, it is represented as:

w · x + b = 0  

where  
- w is the weight vector  
- x is the input feature vector  
- b is the bias  

---

### Support Vectors
Support vectors are the **most important data points** in SVM.
- These points lie closest to the hyperplane  
- They decide the position of the hyperplane  
- Removing other data points does not change the model much  

---

### Margin
- Margin is the distance between the hyperplane and support vectors  
- SVM always tries to maximize the margin  
- Larger margin improves generalization  

---

### Hard Margin SVM
- Used when data is perfectly separable  
- No misclassification allowed  
- Very sensitive to noise  
- Not suitable for real-world data  

---

### Soft Margin SVM
- Allows some misclassification  
- Uses slack variables  
- Controlled by a parameter C  
- Suitable for noisy real-world datasets  

---

### Kernel Trick
When data is not linearly separable, SVM uses the **kernel trick**.
- It maps data into higher-dimensional space  
- Makes non-linear data separable  

Common kernels:
- Linear kernel  
- Polynomial kernel  
- Radial Basis Function (RBF)  
- Sigmoid kernel  

---

### Advantages of SVM
- Works well with high-dimensional data  
- Effective for small datasets  
- Good generalization ability  

---

### Disadvantages of SVM
- Computationally expensive for large datasets  
- Kernel selection is difficult  
- Not suitable for very noisy data  

---

### Applications
- Text classification  
- Image recognition  
- Face detection  
- Bioinformatics  

---

### Conclusion
Support Vector Machine is a powerful algorithm that focuses on finding the best decision boundary. By maximizing the margin and using kernel functions, SVM achieves high accuracy and good performance in many machine learning applications.

---

## Question 2: Explain the Nearest Neighbour Classification Algorithm  
*(10 Marks)*

### Introduction
Nearest Neighbour classification is one of the **simplest supervised machine learning algorithms**. It classifies a data point based on the class labels of nearby data points. It does not build a model during training but stores all the training data.

---

### Basic Concept
The algorithm works on the assumption that:
> Data points that are close to each other belong to the same class.

The most commonly used version is called **k-Nearest Neighbour (k-NN)**.

---

### Working of the Algorithm
1. Store all training data with their labels  
2. Choose a value of k  
3. Calculate distance between test point and training points  
4. Select k nearest data points  
5. Assign the class using majority voting  

---

### Distance Measures
Commonly used distance measures are:
- Euclidean distance  
- Manhattan distance  
- Minkowski distance  
- Cosine similarity  

Feature scaling is important to avoid incorrect distance calculations.

---

### Choice of k
- Small k leads to noise sensitivity  
- Large k smooths the decision boundary  
- Odd values of k avoid tie problems  

---

### Advantages
- Simple and easy to understand  
- No training phase required  
- Works well for multi-class problems  

---

### Disadvantages
- Slow during prediction  
- Requires large memory  
- Sensitive to noisy and irrelevant features  

---

### Applications
- Recommendation systems  
- Pattern recognition  
- Medical diagnosis  
- Image classification  

---

### Conclusion
Nearest Neighbour classification is easy to implement and understand. Though it is computationally expensive at prediction time, it performs well when the dataset is small and decision boundaries are complex.

---

## Question 3: Describe Common Issues in Training Neural Networks  
*(10 Marks)*

### Introduction
Neural networks are widely used in modern machine learning applications. However, training them is difficult due to several problems related to optimization, data, and model structure.

---

### Vanishing Gradient Problem
- Gradients become very small during backpropagation  
- Weight updates become slow  
- Common in deep networks  

**Solution:**  
Use ReLU activation, batch normalization

---

### Exploding Gradient Problem
- Gradients become very large  
- Causes unstable learning  

**Solution:**  
Gradient clipping, proper weight initialization  

---

### Overfitting
- Model performs well on training data  
- Poor performance on test data  

**Solution:**  
Dropout, regularization, early stopping  

---

### Underfitting
- Model is too simple  
- Fails to capture patterns  

**Solution:**  
Increase model complexity, train longer  

---

### Local Minima and Saddle Points
- Loss surface is complex  
- Training may get stuck  

**Solution:**  
Use Adam optimizer, momentum-based methods  

---

### Learning Rate Problems
- High learning rate causes divergence  
- Low learning rate slows training  

**Solution:**  
Learning rate scheduling  

---

### Data-Related Issues
- Noisy data  
- Imbalanced data  
- Insufficient training samples  

---

### Conclusion
Training neural networks involves many challenges. By selecting proper activation functions, optimizers, and regularization techniques, these problems can be reduced and better model performance can be achieved.

---

### END



















# APPLIED MACHINE LEARNING / NLP – ASSIGNMENT  
## M.Tech CSE – I Semester  

---

## Question 1: Illustrate with an example how hold mechanisms support long-distance dependencies  
*(10 Marks)*

### Introduction
In Natural Language Processing (NLP), understanding a sentence often requires remembering information that appears much earlier in the sentence. When a word depends on another word that is far away, it is called a **long-distance dependency**. Handling such dependencies is difficult for basic neural network models. To overcome this problem, **hold mechanisms** are used, especially in advanced recurrent neural networks.

---

### What are Long-Distance Dependencies?
A long-distance dependency occurs when words that are far apart in a sentence are still related to each other.

**Example:**
> *“The boy who was playing in the park with his friends **fell**.”*

Here, the subject **“boy”** appears at the beginning, while the verb **“fell”** appears much later. The model must remember the subject information across many words.

---

### Limitations of Traditional RNNs
Traditional Recurrent Neural Networks (RNNs):
- Process words one at a time
- Gradually forget earlier words
- Suffer from the vanishing gradient problem
- Fail to store important information for long sentences

As a result, they cannot handle long-distance dependencies effectively.

---

### Hold Mechanisms
Hold mechanisms are special structures that help neural networks **store important information for a longer time**. These mechanisms decide:
- What information should be stored
- What information should be forgotten
- When the stored information should be used

They are mainly used in **LSTM (Long Short-Term Memory)** and **GRU (Gated Recurrent Unit)** networks.

---

### How Hold Mechanisms Work
Hold mechanisms use gates to control information flow:
- **Input Gate**: decides which new information should be stored
- **Forget Gate**: removes unnecessary information
- **Output Gate**: decides what information to pass to the next step

Because of these gates, important information is held safely until required.

---

### Example Using LSTM
Sentence:
> *“I lived in Germany for five years and I can speak fluent **German**.”*

Here:
- The word **“Germany”** appears early
- The word **“German”** appears later
- LSTM stores the information about “Germany”
- It holds this information through many words
- Finally, it uses the stored information to correctly predict “German”

This shows how hold mechanisms support long-distance dependencies.

---

### Advantages of Hold Mechanisms
- Prevent loss of important information
- Solve vanishing gradient problem
- Improve understanding of long sentences
- Increase accuracy in NLP tasks

---

### Applications
- Machine translation
- Speech recognition
- Text generation
- Question answering systems

---

### Conclusion
Hold mechanisms play a very important role in handling long-distance dependencies in NLP. By storing and controlling information over long sequences, models like LSTM and GRU perform much better than traditional RNNs.

---

## Question 2: Describe parameter estimation techniques used in language modelling  
*(10 Marks)*

### Introduction
Language modelling is the process of predicting the probability of a word sequence. To build an effective language model, we need to estimate parameters such as word probabilities and model weights. **Parameter estimation** refers to finding the best values of these parameters using training data.

---

### Importance of Parameter Estimation
Good parameter estimation:
- Improves prediction accuracy
- Helps in handling unseen words
- Improves generalization of the model

---

### Maximum Likelihood Estimation (MLE)
Maximum Likelihood Estimation is the most commonly used technique.

- It estimates parameters by maximizing the probability of observed data
- Frequently occurring word sequences get higher probability

**Example:**
If “machine learning” appears many times in training data, MLE assigns a higher probability to this phrase.

**Advantages:**
- Simple and easy to implement
- Works well with large datasets

**Limitation:**
- Assigns zero probability to unseen words

---

### Smoothing Techniques
Smoothing techniques are used to avoid zero probability problems.

#### Laplace (Add-One) Smoothing
- Adds one to every word count
- Ensures no probability becomes zero
- Simple but may over-smooth data

#### Good-Turing Smoothing
- Adjusts probabilities of unseen events
- More accurate than Laplace smoothing
- Used in practical systems

---

### Backoff and Interpolation Methods
- **Backoff**: Uses lower-order models when higher-order data is missing
- **Interpolation**: Combines probabilities from multiple n-gram models

These techniques improve robustness.

---

### Neural Network-Based Estimation
Modern language models use neural networks:
- Parameters are weights of the network
- Learned using gradient descent
- Loss minimized using backpropagation
- Optimizers like Adam and SGD are used

---

### Conclusion
Parameter estimation is a key part of language modelling. Techniques like MLE, smoothing, backoff, and neural network-based learning help in building accurate and reliable language models.

---

## Question 3: Differentiate between Cross-Lingual Information Retrieval (CLIR) and Multilingual Information Retrieval (MLIR)  
*(10 Marks)*

### Introduction
Information Retrieval systems help users find relevant information from large collections of documents. When multiple languages are involved, two important approaches are used: **Cross-Lingual Information Retrieval (CLIR)** and **Multilingual Information Retrieval (MLIR)**. Although they sound similar, they differ in purpose and operation.

---

### Cross-Lingual Information Retrieval (CLIR)
In CLIR:
- The user query is written in one language
- Documents are written in another language
- Translation is required to match query and documents

**Example:**  
An English query retrieving documents written in Spanish.

CLIR is useful when users do not know the document language.

---

### Multilingual Information Retrieval (MLIR)
In MLIR:
- Queries and documents may exist in multiple languages
- System retrieves documents in several languages at once
- No restriction on language

**Example:**  
A single query retrieving documents in English, Hindi, and French.

MLIR is useful for global and international information systems.

---

### Detailed Comparison Between CLIR and MLIR

| Aspect | CLIR | MLIR |
|------|------|------|
| Basic Idea | Searches documents written in a language different from the query language | Searches documents written in multiple languages |
| Query Language | Single language | One or more languages |
| Document Language | Different from query language | Multiple languages |
| Translation Requirement | Mandatory (query or document translation) | Optional, depends on system |
| Output Language | Usually one target language | Multiple languages |
| System Complexity | Comparatively lower | Higher due to many languages |
| Challenges | Translation errors, loss of meaning | Language detection, ranking across languages |
| Use Case | Searching foreign-language documents | Global search platforms |

---

### Conclusion
CLIR focuses on retrieving information across language boundaries, while MLIR focuses on retrieving information in many languages at the same time. Both are important for handling multilingual data, but MLIR systems are more complex than CLIR systems.

---

### END


