json.extract! announcement, :id, :title, :description, :deadline, :tags, :created_at, :updated_at
json.url announcement_url(announcement, format: :json)