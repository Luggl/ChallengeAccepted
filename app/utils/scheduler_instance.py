from apscheduler.schedulers.background import BackgroundScheduler

#Scheduler wird benötigt um Deadline-Überschreitung und Task-Erstellung zu übernehmen
#Wird außerhalb erzeugt, um keine Circular Import Errors zu erzeugen
scheduler = BackgroundScheduler()
