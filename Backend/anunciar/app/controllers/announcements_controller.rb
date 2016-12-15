class AnnouncementsController < ApplicationController
  before_action :set_announcement, only: [:show, :edit, :update, :destroy]
  before_action :authenticate_student!
  # GET /announcements
  # GET /announcements.json
  def index
    @announcements = Announcement.all.order("created_at DESC")
  end

  # GET /announcements/1
  # GET /announcements/1.json
  def show
  end
  private
    # Use callbacks to share common setup or constraints between actions.
    def set_announcement
      @announcement = Announcement.find(params[:id])
    end

    # Never trust parameters from the scary internet, only allow the white list through.
    def announcement_params
      params.require(:announcement).permit(:title, :description, :deadline, :tags)
    end
end
