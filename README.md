# Operating Systems – 10 Marks Answers

---

## 1. Handling of Deadlocks and Various Deadlock Handling Strategies

### Introduction
A **deadlock** is a situation in an operating system where a set of processes are permanently blocked because each process is holding one or more resources and waiting for additional resources held by other processes. Since none of the processes can proceed, the system enters an indefinite waiting state. Deadlocks usually occur in multiprogramming environments where multiple processes compete for limited resources such as CPU, memory, files, and I/O devices.

---

### Necessary Conditions for Deadlock
Deadlock can occur only if all the following four conditions hold simultaneously:

1. **Mutual Exclusion**  
   At least one resource must be non-shareable, meaning only one process can use it at a time.

2. **Hold and Wait**  
   A process holding at least one resource is waiting to acquire additional resources held by other processes.

3. **No Preemption**  
   Resources cannot be forcibly taken from a process and must be released voluntarily.

4. **Circular Wait**  
   A circular chain of processes exists, where each process holds a resource required by the next process in the chain.

---

### Deadlock Handling Strategies

#### 1. Deadlock Prevention
Deadlock prevention ensures that at least one of the necessary conditions for deadlock never occurs. This can be achieved by eliminating mutual exclusion where possible, forcing processes to request all resources at once to avoid hold and wait, or enforcing a strict ordering of resource requests to prevent circular wait.

**Advantages**
- Guarantees deadlock-free system

**Disadvantages**
- Poor resource utilization
- Possible starvation

---

#### 2. Deadlock Avoidance
Deadlock avoidance dynamically checks resource allocation requests and ensures that the system always remains in a **safe state**. The most commonly used avoidance technique is the **Banker’s Algorithm**, which requires prior knowledge of maximum resource requirements of each process.

**Advantages**
- Better resource utilization than prevention

**Disadvantages**
- High computational overhead
- Requires advance information

---

#### 3. Deadlock Detection and Recovery
In this approach, deadlocks are allowed to occur and are detected periodically using detection algorithms such as resource allocation graphs. Once detected, recovery is performed by terminating deadlocked processes or preempting resources.

**Advantages**
- No restriction on resource requests

**Disadvantages**
- Recovery cost is high
- Possible loss of data

---

#### 4. Deadlock Ignorance
The operating system ignores deadlocks assuming they are rare. This approach is used in general-purpose operating systems such as UNIX and Windows.

---

### Conclusion
Deadlock handling is a crucial aspect of operating system design. Each strategy has its own advantages and limitations, and the choice of method depends on system requirements and performance considerations.

---

## 2. Process Scheduling in Multiprocessor Operating Systems – Compare Scheduling Approaches

### Introduction
A **multiprocessor operating system** consists of two or more processors that share system resources such as main memory, secondary storage, and I/O devices. Unlike uniprocessor systems, multiprocessor systems can execute multiple processes simultaneously, which improves performance and throughput. However, process scheduling in such systems is more complex due to challenges like **load balancing**, **processor affinity**, **synchronization**, and **cache consistency**.

The main goal of multiprocessor scheduling is to distribute processes efficiently among processors to maximize system performance while maintaining fairness.

---

### Objectives of Multiprocessor Scheduling
- Maximize overall CPU utilization  
- Improve system throughput  
- Minimize response time and waiting time  
- Achieve effective load balancing  
- Reduce process migration overhead  
- Improve cache performance  

---

### Scheduling Approaches

#### 1. Asymmetric Multiprocessing (AMP)
In **Asymmetric Multiprocessing**, one processor is designated as the **master processor**. The master processor is responsible for scheduling, I/O handling, and system management, while the remaining processors execute user-level processes.

**Advantages**
- Simple design and easy implementation  
- Reduced synchronization complexity  

**Disadvantages**
- Master processor becomes a bottleneck  
- Poor scalability as the number of processors increases  

---

#### 2. Symmetric Multiprocessing (SMP)
In **Symmetric Multiprocessing**, all processors are treated equally. Each processor independently performs scheduling and can execute both system and user processes. Any process can run on any processor.

**Advantages**
- Better load balancing  
- High scalability  
- No single point of failure  

**Disadvantages**
- Increased synchronization overhead  
- More complex scheduling algorithms  

---

#### 3. Global Scheduling
In global scheduling, all ready processes are stored in a **single global ready queue**. Any processor can select a process from the queue for execution.

**Advantages**
- Simple scheduling mechanism  
- Dynamic load balancing  

**Disadvantages**
- High contention for the global queue  
- Frequent process migration affects cache performance  

---

#### 4. Partitioned Scheduling
In partitioned scheduling, processes are **statically assigned** to specific processors. Each processor maintains its own local ready queue and schedules only the processes assigned to it.

**Advantages**
- Reduced scheduling overhead  
- Better cache locality  
- No contention among processors  

**Disadvantages**
- Load imbalance may occur  
- Less flexible compared to global scheduling  

---

#### 5. Processor Affinity
Processor affinity ensures that a process prefers to execute on the same processor it previously ran on. This improves cache utilization and reduces performance loss due to process migration.

- **Soft Affinity**: OS attempts to keep the process on the same processor  
- **Hard Affinity**: Process is permanently bound to a processor  

---

### Conclusion
Multiprocessor scheduling plays a vital role in overall system performance. Modern operating systems generally use **SMP combined with processor affinity** to balance scalability, performance, and efficient resource utilization.

---

## 3. Distributed Shared Memory (DSM): Architecture, Motivation, Memory Coherence Protocols, and Design Issues

### Introduction
**Distributed Shared Memory (DSM)** is a memory management model that provides the abstraction of a shared memory system over physically distributed memory. In DSM, memory is physically distributed across multiple machines, but logically presented as a single shared address space. This allows processes running on different machines to communicate through shared variables rather than explicit message passing.

---

### Motivation for DSM
The main motivations for DSM include:
- Simplifying parallel and distributed programming  
- Providing a shared-memory programming model  
- Improving scalability of distributed systems  
- Cost efficiency compared to hardware shared memory systems  

---

### DSM Architecture

#### Based on Data Organization
- **Page-based DSM** – Data sharing occurs at page level  
- **Object-based DSM** – Objects are shared instead of pages  
- **Variable-based DSM** – Individual variables are shared  

#### Based on Implementation
- **Hardware DSM** – Implemented using special hardware support  
- **Software DSM** – Implemented at operating system or middleware level  
- **Hybrid DSM** – Combination of hardware and software techniques  

---

### Memory Coherence in DSM
Memory coherence ensures that all processes observe a **consistent and correct view of shared data**, even though multiple copies of data may exist across different nodes. Maintaining coherence is one of the major challenges in DSM systems.

---

### Memory Coherence Protocols

#### 1. Write Invalidate Protocol
Before a process updates a shared data item, all other copies of that data are invalidated. Other nodes must fetch the updated data before accessing it again.

**Advantages**
- Reduced communication overhead  
- Efficient for write-intensive applications  

---

#### 2. Write Update Protocol
When a write occurs, the updated value is immediately propagated to all nodes holding a copy.

**Advantages**
- Faster subsequent read operations  

**Disadvantages**
- High network communication cost  

---

#### 3. Release Consistency
In release consistency, updates to shared memory are propagated only at synchronization points such as locks and barriers. This reduces unnecessary communication and improves performance.

---

### Design Issues in DSM
- Choice of memory consistency model  
- Granularity of data sharing  
- False sharing problem  
- Network latency  
- Scalability and fault tolerance  

---

### Advantages of DSM
- Transparent shared memory abstraction  
- Simplifies distributed and parallel programming  
- Platform independence  

---

### Conclusion
DSM effectively bridges the gap between shared memory and distributed systems. However, efficient memory coherence, synchronization mechanisms, and latency management are essential for achieving high performance in DSM systems.
