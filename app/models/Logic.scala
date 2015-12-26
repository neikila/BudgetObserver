package models

/**
  * Created by neikila on 25.12.15.
  */
object Logic {
  def getPurchases(username: String) = {
    DBAccess.getPurchases(username)
  }

  def savePurchase(purchase: Purchase) = {
    DBAccess.saveInDB(purchase)
  }

  def getUsersInGroup(groupId: Int) = {
    DBAccess.getUsersInGroup(groupId)
  }

  def getGroup(groupID: Int) = {
    DBAccess.getGroup(groupID)
  }

  def createGroup(groupName: String, author: String) = {
    DBAccess.createGroup(author, groupName)
    DBAccess.includeUserInGroup(author, groupName)
  }
}
