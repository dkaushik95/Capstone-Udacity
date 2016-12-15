module Api
	module V1
		class AnnouncementsController < ApplicationController

			before_action :set_announcement, only: [:show, :edit, :update, :destroy]
			# http_basic_authenticate_with name:'admin', password: 'secret'
			respond_to :json
			def index
				respond_with Announcement.all.order("created_at DESC")
			end
			def show
				respond_with Announcement.find(params[:id])
			end
			def create
				respond_with Announcement.create(announcement_params)
			end
			def update
				respond_with Announcement.update(params[:id], announcement_params)
			end
			def destroy
				respond_with Announcement.destroy(params[:id])
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
	end
end
