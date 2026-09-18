# Attendance Tracker with Prediction — Project Report

## 1. Project Overview

Attendance Tracker with Prediction is a simple attendance management project designed to help students record and monitor subject-wise attendance and obtain attendance-related predictions.

The project combines attendance tracking with a basic AI/ML-based prediction component.

## 2. Main Functional Modules

The project includes the following major functional areas:

1. **Add Subject** — records attendance information for a subject.
2. **View Attendance** — displays stored attendance information.
3. **Basic Prediction** — provides attendance-related calculations/predictions.
4. **AI Prediction (ML)** — uses a machine-learning model for prediction.
5. **Data Storage** — maintains attendance information for later use.

## 3. Technologies Used

- Python
- JSON for data storage
- NumPy
- Scikit-learn
- Linear Regression

## 4. System Workflow

The general workflow is:

```text
Start
  |
  v
Display Menu
  |
  +--> Add Subject ------> Store Attendance Data
  |
  +--> View Attendance --> Display Attendance
  |
  +--> Basic Prediction -> Calculate Prediction
  |
  +--> AI Prediction ----> ML Model -> Prediction
  |
  +--> Exit
```

## 5. Testing and Results

The project should be tested using different attendance values, including:

- Valid attendance records.
- Multiple subjects.
- Low attendance values.
- High attendance values.
- Prediction using previously stored attendance data.
- Invalid or incomplete input where applicable.

### Result

The expected result is that the application accepts attendance information, stores it, displays the recorded attendance, and provides the available prediction features through the menu.

> Note: This section describes the testing scope and expected behaviour. Specific numerical test results should be added after running the final version of the project.

## 6. Screenshots / Evidence

Add screenshots of the following after running the project:

1. Main menu.
2. Adding a subject.
3. Viewing attendance.
4. Basic prediction.
5. AI/ML prediction.
6. Stored data/output.

Suggested filenames:

```text
screenshots/
├── main_menu.png
├── add_subject.png
├── attendance_view.png
├── basic_prediction.png
└── ml_prediction.png
```

## 7. Challenges Faced

Possible implementation challenges include maintaining consistent attendance data, calculating attendance correctly, handling stored data, and integrating the machine-learning prediction component with the main application workflow.

## 8. Future Enhancements

Possible future improvements include:

- Graphical user interface.
- Attendance charts and dashboards.
- Notifications for low attendance.
- More advanced prediction models.
- Exporting attendance reports.
- Cloud-based data storage.

## 9. Conclusion

The project demonstrates how attendance management can be combined with prediction to make academic attendance tracking more convenient. It applies Python programming, data storage, numerical processing, and machine-learning concepts in a practical application.
