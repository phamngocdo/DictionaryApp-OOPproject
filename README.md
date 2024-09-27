# Dolingo - English Learning Application

<p align="center">
  <img src="/src/main/resources/graphic/logo.png" alt="App Logo">
</p>

## 1. Introduction

**Dolingo** is an interactive English learning app designed to help users improve their vocabulary, grammar, and speaking skills. The app provides various engaging features such as games, a dictionary, text-to-speech functionality, and translation tools to enhance the learning experience.

### Key Features

- **Dictionary**: Search for word definitions by using Trie algorithm.
- **Translate**: Translate text between English and Vietnamese.
- **Bookmarks**: Save favorite words for quick access.
- **Add Words**: Add new words along with their definitions to the dictionary.
- **Games**: Improve your vocabulary knowledge while playing games.
- **Settings**: Customize theme, language and send feedback to us.

---

## 2. Technologies Used

The following technologies are utilized to build Dolingo:

- **Java**: Backend logic and core app functionality.
- **JavaFX**: User interface design and interactions.
- **Maven**: Build automation and dependency management.
  
---

## 3. Database

Dolingo uses an SQLite database to store vocabularies and their explainations, examples. The database schema can be found [here](https://github.com/png261/english-vietnamese-database/) for reference.

---

## 4. Installation Guide

### Requirements

To run this app, you will need Java 17 or higher Coding Pack: https://aka.ms/vscode-java-installer-win
and one of the following IDE:
- **Visual Studio Code**: https://code.visualstudio.com/docs/java/java-tutorial
- **IntelliJ IDEA**: https://www.jetbrains.com/idea/download/?section=windows

#### Step-by-step Installation

Step 1. **Clone the repository**:
   ```bash
   git clone https://github.com/phamngocdo/DictionaryApp-OOPproject.git
   ```
Step 2. **Run the application**:
   - Open the project in your preferred IDE.
   - Navigate to package src\main\java\app\main\ and run the RunApp class to start the application.  

**Note**: This project uses UTF-8 encoding for proper handling of Vietnamese.

For Visual Studio settings:
<p align="center">
<img src="/src/main/resources/graphic/utf8vscode.png" alt="UTF8 VScode">
</p>
For IntelliJ IDEA:
<p align="center">
  <img src="/src/main/resources/graphic/utf8idea.png" alt="UTF8 IDEA">
</p>
