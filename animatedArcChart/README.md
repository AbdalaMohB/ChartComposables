# Animated Arc Chart

This is a circular chart for displaying data comparatively.

This composable takes data, NOT angles.

## Input Parameters
* modifier: Only affects canvas.
* itemNames: The list of value names to be displayed.
* values: The list of values being charted
* colors: The colors of assigned to each value
* strokeThickness: Thickness of the arc displaying the data
* arcMaxAngle: Defines the max angle of the chart. Ex: If it equals 360, the chart will be full circle. 180 would make half a circle.
* animationDurationMilliseconds: animation duration.
* parallelAnimation: A boolean for deciding whether to animate the arcs one by one or all at once
* textStyle: Style for the chart info text
* dotRadius: Radius for chart info color dot
* referenceSpacing: Spaces the info section items apart
* graphSize: Assigns a custom size for the chart instead of a computed one.

## Helper Functions
* getAnglesFromItems(): This function takes the data you wish to display and returns it as a list of angles based on the arcMaxAngleVariable
* getSweepWithAnimatable(): Returns the animation's target angle as well as the the animatable value.
* getStartingPoints(): Returns the starting angles of each section of the chart to make each one end into the other


<img width="444" height="894" alt="demo" src="https://github.com/user-attachments/assets/09fb3544-2ae2-4144-a19d-0cddb547f7f0" />



3000 millisecond non-parallel animation:

[demonstration.webm](https://github.com/user-attachments/assets/b2fa4df1-9200-4b4d-a5a2-069f2df43256)

