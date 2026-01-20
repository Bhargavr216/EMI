# APPLIED MACHINE LEARNING / NLP – ASSIGNMENT  
## M.Tech CSE – I Semester  

---

## Question 1: Illustrate with an example how hold mechanisms support long-distance dependencies  
*(10 Marks)*

### Introduction
In Natural Language Processing (NLP), understanding a sentence often requires remembering information that appears much earlier in the sentence. When a word depends on another word that is far away, it is called a **long-distance dependency**. Handling such dependencies is difficult for basic neural network models. To overcome this problem, **hold mechanisms** are used, especially in advanced recurrent neural networks.

---

### What are Long-Distance Dependencies?
A long-distance dependency occurs when words that are far apart in a sentence are still related to each other. The model must remember earlier information until it reaches the related word later in the sentence.

**Example:**
> “The boy who was playing in the park with his friends **fell**.”

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
> “I lived in Germany for five years and I can speak fluent **German**.”

Here:
- The word **“Germany”** appears early  
- The word **“German”** appears later  
- LSTM stores the information about “Germany”  
- It holds this information through many words  
- Finally, it uses the stored information to correctly predict “German”  

This clearly explains how hold mechanisms support long-distance dependencies.

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

### Key Points / Exam Notes
- Long-distance dependencies occur when related words are far apart  
- Traditional RNNs fail due to vanishing gradients  
- Hold mechanisms store and control important information  
- LSTM and GRU effectively handle long sequences  

---

## Question 2: Analyze the limitations of n-gram models in capturing long-range dependencies  
*(10 Marks)*

### Introduction
N-gram models are one of the earliest approaches used in language modelling. An n-gram model predicts the next word based on the previous **n − 1** words. For example, a bigram model considers one previous word, while a trigram model considers two previous words. Although n-gram models are simple and easy to implement, they have several limitations in capturing **long-range dependencies**.

---

### Fixed Context Window
The most important limitation of n-gram models is their **fixed context window**. An n-gram model can only look at a limited number of previous words and ignores all earlier words.

For example, a trigram model cannot capture relationships beyond two words, which limits its ability to handle long sentences.

---

### Inability to Capture Long-Range Dependencies
Long-range dependencies occur when the meaning of a word depends on another word that is far away in the sentence.

**Example:**
> “The book that you gave me yesterday **is** very interesting.”

Here, the subject “book” and the verb “is” are far apart. An n-gram model fails to capture this dependency.

---

### Data Sparsity Problem
As the value of **n** increases, the number of possible n-grams increases rapidly. Many valid word sequences may never appear in the training data. This causes the **data sparsity problem**, where meaningful sequences get zero or very low probability.

---

### Lack of Semantic and Syntactic Understanding
N-gram models rely only on word frequency counts. They do not understand:
- Grammar rules  
- Sentence meaning  
- Long-distance relationships  

This limits their performance on complex language tasks.

---

### High Storage and Computation Cost
Higher-order n-gram models require large memory and computational power. Even with this cost, they still fail to capture long-range dependencies effectively.

---

### Key Points / Exam Notes
- N-gram models use a fixed-size context window  
- Cannot remember information beyond n − 1 words  
- Suffer from data sparsity and zero probability issues  
- Lack semantic understanding of language  

---

## Question 3: Differentiate between Cross-Lingual Information Retrieval (CLIR) and Multilingual Information Retrieval (MLIR)  
*(10 Marks)*

### Introduction
Information Retrieval systems help users find relevant information from large collections of documents. When multiple languages are involved, two important approaches are used: **Cross-Lingual Information Retrieval (CLIR)** and **Multilingual Information Retrieval (MLIR)**.

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
| Translation Requirement | Mandatory | Optional, depends on system |
| Output Language | Usually one target language | Multiple languages |
| System Complexity | Comparatively lower | Higher due to many languages |
| Challenges | Translation errors, loss of meaning | Language detection, ranking across languages |
| Use Case | Searching foreign-language documents | Global search platforms |

---

### Key Points / Exam Notes
- CLIR works across different languages  
- MLIR supports multiple languages together  
- Translation is compulsory in CLIR  
- MLIR systems are more complex to design  

---

### END
