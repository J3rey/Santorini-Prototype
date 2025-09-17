# FIT3077 Repository

Team Name: Let's Go

# Santorini Game Implementation

## Overview

This is a comprehensive Java implementation of the board game Santorini, developed as a group project for FIT3077. Santorini is a strategic building game where players control workers to construct buildings and reach the third level to achieve victory. Our implementation features a complete game engine with god powers, timer system, interactive tutorial, and polished user interface.

## Game Features

### Core Gameplay

- **Complete Santorini Rules Implementation** - Full board game mechanics with proper movement and building validation
- **Multi-Player Support** - 2-4 players with team-based gameplay
- **Variable Board Sizes** - Support for 5x5 and 7x7 game boards
- **Turn-Based Timer System** - 15-minute default timer per player with timeout detection
- **Victory Conditions** - Multiple win conditions including standard victory and elimination

### God Powers System

- **Extensible God Framework** - Strategy pattern implementation for easy addition of new gods
- **Available Gods:**
  - **Artemis** - Move twice per turn (cannot return to starting position)
  - **Demeter** - Build twice per turn (cannot build on same space)
  - **Triton** - Move again when reaching perimeter spaces
- **God Selection Interface** - Clean UI for choosing god powers before gameplay

### User Interface

- **Modern Swing GUI** - Consistent styling throughout all screens
- **Interactive Tutorial** - Step-by-step guided learning experience
- **Real-Time Timer Display** - Visual countdown with color-coded warnings
- **Game Statistics Tracking** - Moves, builds, domes, and god power usage
- **Victory Screen** - Comprehensive end-game statistics and celebration

### Advanced Features

- **Action History System** - Command pattern with undo support
- **State Management** - Robust turn state tracking and validation
- **Error Handling** - Comprehensive validation and user feedback
- **Observer Pattern** - Decoupled event handling for timers and game events

## Project Architecture

### Package Structure

```
SantoriniGame/
├── controller/              # MVC Controllers and Presenters
│   ├── GameController      # Main game flow controller
│   └── GodSelectionPresenter # God selection MVP presenter
├── god/                    # God Powers (Strategy Pattern)
│   ├── God                 # Abstract base class
│   ├── GodPower           # Interface for god abilities
│   ├── GodFactory         # Factory for god creation
│   ├── Artemis, Demeter, Triton # Concrete god implementations
│   └── PowerStatus        # Enum for god power states
├── model/                  # Game Logic and Data
│   ├── action/            # Command Pattern for Actions
│   │   ├── Action         # Abstract action base
│   │   ├── MoveAction, BuildAction, etc. # Concrete actions
│   │   └── ActionType     # Action type enumeration
│   ├── board/             # Board Components
│   │   ├── Board          # Game board logic
│   │   ├── BoardTile      # Individual tile logic
│   │   ├── Building       # Building level management
│   │   └── TileUI         # Tile visual representation
│   ├── game/              # Core Game Logic
│   │   ├── GameModel      # Central game state
│   │   ├── SantoriniGame  # Main game UI
│   │   ├── TurnState      # Turn management
│   │   ├── TurnPhase      # Turn phase enumeration
│   │   └── GameStatistics # Statistics tracking
│   ├── player/            # Player and Team Management
│   │   ├── Player         # Player representation
│   │   ├── Team           # Team coordination
│   │   ├── Worker         # Game piece logic
│   │   └── WorkerGender   # Worker identification
│   ├── timer/             # Timer System (Observer Pattern)
│   │   ├── TimerManager   # Timer coordination
│   │   ├── PlayerTimer    # Individual player timers
│   │   └── TimerListener  # Timer event interface
│   └── victory/           # Victory Conditions (Strategy Pattern)
│       ├── VictoryCondition # Abstract victory base
│       ├── StandardVictoryCondition # Win by reaching level 3
│       ├── DefaultVictoryCondition  # Last team standing
│       └── TimeoutVictoryCondition  # Timeout victory
├── tutorial/               # Interactive Tutorial System
│   ├── TutorialController # Tutorial-specific game controller
│   ├── TutorialStateManager # Tutorial progression logic
│   ├── TutorialStep       # Tutorial step definitions
│   ├── TutorialAction     # Tutorial action types
│   └── TutorialUI         # Tutorial user interface
├── ui/                    # User Interface Components
│   ├── BoardUI            # Game board visualization
│   ├── GodSelection       # God selection interface
│   ├── GodSelectionModel  # God selection data model
│   ├── GodSelectionView   # God selection view interface
│   ├── PlayerBoardSelection # Game setup interface
│   ├── SantoriniMainMenu  # Main menu navigation
│   ├── TimerUIPanel       # Real-time timer display
│   └── VictoryScreen      # End game celebration
└── SantoriniGameLauncher  # Application entry point
```

### Design Patterns Implemented

- **Model-View-Controller (MVC)**: Clean separation between game logic, user interface, and control flow
- **Model-View-Presenter (MVP)**: Used specifically for god selection with testable presenter logic
- **Command Pattern**: All game actions implemented as commands with undo support
- **Strategy Pattern**: God powers and victory conditions implemented as interchangeable strategies
- **Observer Pattern**: Timer events and game state notifications
- **Factory Pattern**: God creation and instantiation
- **State Pattern**: Turn phase management and game state transitions
- **Singleton Pattern**: God factory for centralized god management

### SOLID Principles

- **Single Responsibility Principle**: Each class has a focused, single purpose
- **Open/Closed Principle**: Easy to add new gods, victory conditions, and UI components
- **Liskov Substitution Principle**: Gods and victory conditions are fully interchangeable
- **Interface Segregation Principle**: Clean, focused interfaces like GodPower and TimerListener
- **Dependency Inversion Principle**: High-level modules depend on abstractions, not concretions

## Game Rules

### Basic Gameplay

1. **Setup**: Players select gods and are randomly placed on the board with two workers each
2. **Turn Structure**: Each turn consists of three phases:
   - **Select Worker**: Choose one of your workers to move
   - **Move**: Move the selected worker to an adjacent space
   - **Build**: Build one level on a space adjacent to the worker's new position
3. **Movement Rules**:
   - Workers can move to any of the 8 adjacent spaces (including diagonally)
   - Workers can move up a maximum of 1 level
   - Workers can move down any number of levels
   - Workers cannot move to occupied spaces or spaces with domes
4. **Building Rules**:
   - Buildings progress from level 0 → 1 → 2 → 3 → dome
   - Cannot build on occupied spaces or existing domes
   - Must build adjacent to the worker that just moved

### Victory Conditions

- **Standard Victory**: Move one of your workers UP to the third level of a building
- **Default Victory**: Be the last team remaining (opponents eliminated)
- **Timeout Victory**: Win when an opponent runs out of time

### God Powers

God powers provide unique abilities that modify the basic rules:

- **Artemis (Movement Enhancement)**:
  - _Timing_: Your Move
  - _Power_: Your worker may move one additional time, but not back to its initial space
- **Demeter (Building Enhancement)**:

  - _Timing_: Your Build
  - _Power_: Your worker may build one additional time, but not on the same space

- **Triton (Perimeter Movement)**:
  - _Timing_: Your Move
  - _Power_: Each time your worker moves into a perimeter space, it may immediately move again

### Timer System

- **Default Time Limit**: 15 minutes per player
- **Visual Feedback**: Color-coded timer display (green → yellow → red)
- **Timeout Handling**: Automatic team elimination when time expires
- **Turn Switching**: Automatic timer management between players

## Getting Started

### Prerequisites

- Java 11 or higher
- Java Swing (included in standard JDK)

### Running the Game

1. Compile the Java source files
2. Run the main class: `SantoriniGameLauncher`
3. Navigate through the main menu to start a game or tutorial

### Game Flow

1. **Main Menu**: Choose between starting a game, tutorial, how to play, or settings
2. **Game Setup**: Select number of players (2-4) and board size (5x5 or 7x7)
3. **God Selection**: Each player chooses their god power
4. **Gameplay**: Take turns moving workers and building structures
5. **Victory**: Game ends when a player reaches level 3 or opponents are eliminated

## Tutorial System

The game includes a comprehensive interactive tutorial that teaches:

- Worker selection and movement mechanics
- Building rules and restrictions
- God power usage (using Artemis as example)
- Turn management and game flow
- Victory conditions and strategy

The tutorial provides real-time feedback and step-by-step guidance for new players.

## Technical Highlights

### Architecture Quality

- **Clean Code**: Comprehensive Javadoc documentation throughout
- **Error Handling**: Robust validation and user feedback
- **Performance**: Efficient algorithms for move/build validation
- **Maintainability**: Well-structured packages and clear responsibilities
- **Extensibility**: Easy to add new gods, victory conditions, and UI features

### Testing Considerations

- **MVP Pattern**: Testable presenter logic separated from view
- **Command Pattern**: Actions can be easily unit tested
- **Dependency Injection**: Controllers accept model dependencies for testing
- **State Management**: Clear state transitions enable integration testing

## Contributors

- **Jeremia Yovinus**

## Project Status

✅ **Complete** - Full implementation with all planned features

- Core gameplay mechanics
- God power system with 3 gods
- Timer system with visual feedback
- Interactive tutorial
- Polished user interface
- Comprehensive documentation
