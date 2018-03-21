# Shopalize
##Android Receipt Storage Application

This project was undertaken as part of a Ubiquitous Computing module under Dr. Gregory O'Hare.
This project was completed as part of a team consisting of myself, Jake Kavanagh and Thomas Neville.

As part of the project the following was undertaken:

- Setting up of a Python web scraper that parsed JSON data and added it to an AWS RDS MySQL instance
- Development and hosting of Python Flask application via AWS EC2 instance
- Use of DublinBikes and OpenWeatherMaps APIs
- Use of AWS S3 for backup storage

## Application Screenshots
Main Screen on Login - Colour of screen changes based upon the users current monthly spend compared to their average monthly spend.
<img src="./Main Screen.PNG"/>

Shopping List - Contains entries that the user has added. The bottom button allows the user to items based upon the user's past purchases. The user can also add item by clicking the button in the bottom right. From here the user can add an item by text or take a photo of the item which adds the item to the list.
<img src="./Shopping List.PNG"/>

Image Gallery - This allows the user to take photos of their receipts. From here the user can see their stored receipts. 
<img src="./Receipt Gallery.PNG"/>

