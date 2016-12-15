class CreateAusers < ActiveRecord::Migration[5.0]
  def change
    create_table :ausers do |t|
      t.string :provider
      t.string :uid
      t.string :name
      t.string :email

      t.timestamps
    end
  end
end
