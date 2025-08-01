# Use official Python image as base
FROM python:3.9-slim

# Set working directory inside the container
WORKDIR /app

# Copy the Flask app code into the container
COPY app.py .

# Install Flask
RUN pip install Flask

# Expose port 5000 to the outside
EXPOSE 5000

# Command to run the app
CMD ["python", "app.py"]

