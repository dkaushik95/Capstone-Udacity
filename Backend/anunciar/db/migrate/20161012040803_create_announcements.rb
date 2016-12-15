class CreateAnnouncements < ActiveRecord::Migration[5.0]
  def change
    create_table :announcements do |t|
      t.string :title
      t.string :description
      t.date :deadline
      t.string :tags

      t.timestamps
    end
  end
end
